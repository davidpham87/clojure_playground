(ns user
  (:require [clojure.core.async :as a :refer (<! >! go go-loop chan)]))

(def worker-chan (a/chan 2))

(defn start-process [c]
  (go-loop []
    (when-let [p (a/<! c)]
      (.start p)
      (.waitFor p)
      (recur))))

(defn start-process-2 [c]
  (go-loop [x (a/<! c)]
    (when x
      (println x)
      (a/<! (a/timeout (rand-int 1000)))
      (recur (a/<! c)))))


(defn print-result-chan []
  (let [c (chan 999)]
    (a/thread
      (loop []
        (when-let [s (a/<!! c)]
          (println s)
          (recur))))
    c))

(def print-chan (print-result-chan))

(defn worker-chan []
  (let [c (chan)]
    (go-loop []
      (when-let [x (<! c)]
        (>! print-chan (str "Working:  " x))
        (<! (a/timeout (rand-int 1000)))
        (>! print-chan (str "Finished:     " x))
        (recur)))
    c))

(defn alts-pool-vec [pool value]
  (mapv #(vector %1 %2) pool (repeat value)))

(defn master-coordinator [c pool]
  (go-loop []
    (when-let [x (<! c)]
      (>! print-chan (str "Received:           " x))
      (a/alts! (alts-pool-vec pool x))
      (recur)))
  c)

(def worker-pool (mapv (fn [_] (worker-chan)) (range 5)))
(def master-channel (chan 10))

(master-coordinator master-channel worker-pool)

(do
  (go
    (a/<! (a/timeout 100))
    (doseq [x (range 20)]
      (a/>! master-channel x)
      (>! print-chan (str "sent " x))
      )
    (>! print-chan "Finished sending 20"))

  (go
    (a/<! (a/timeout 50))
    (doseq [x (range 20 40)]
      (a/>! master-channel x)
      (>! print-chan (str "sent " x)))
    (>! print-chan "Finished sending 40"))

  (go
    (doseq [x (range 40 60)]
      (a/>! master-channel x)
      (>! print-chan (str "sent " x)))
    (>! print-chan "Finished sending 60")))

(a/put! print-chan 10 (fn [x] (println "Complete put:" x)))
(a/take! print-chan (fn [x] (println "Complete take:" x)))

(range 10)
