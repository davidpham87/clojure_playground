(ns ch.kafka.core
  (:require
   [clojure.core.async :refer (chan close! <!! >!! go <! >! go-loop)]
   [cognitect.transit :as t]
   [ketu.async.source :as source]
   [ketu.async.sink :as sink]
   [portal.api :as p])
  (:import
   java.nio.charset.StandardCharsets
   [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defn from-bytes
  "Converts a byte array to a string."
  [^bytes data]
  (String. data StandardCharsets/UTF_8))

(defn to-bytes
  "Converts a string to a byte array."
  [data]
  (.getBytes ^String data StandardCharsets/UTF_8))

(defn ->transit [o]
  (let [baos (ByteArrayOutputStream. 1024)
        writer (t/writer baos :json)
        _ (t/write writer o)]
    (.toByteArray baos)))

(defn ->edn [byte-array]
  (let [bais (ByteArrayInputStream. byte-array)
        reader (t/reader bais :json)]
    (t/read reader)))

(def baos (ByteArrayOutputStream. 1024))


(def >greet (chan 10))
(def <names3 (chan 10))

(def source (atom nil))
(def debug (atom nil))

(let [source-opts {:name "new-consumer"
                   :brokers "localhost:9092"
                   :topic "my.third.topics"
                   :group-id "consumer"
                   :value-type :byte-array
                   :shape :value}
      _ (reset! source (source/source <names3 source-opts))
      sink-opts {:name "new-producer"
                 :brokers "localhost:9092"
                 :topic "my.third.topics"
                 :value-type :byte-array
                 :shape :value}
      sink (sink/sink >greet sink-opts)]

  (go-loop []
    (let [v (->edn (<! <names3))]
      (println v)
      (reset! debug v)
      (when v
        (println "End")
        (recur)))))


(go
  (>! >greet "hello"))

(>!! >greet "This is so holy wowo!")


(go
  (doseq [s ["I am "
             {:a 3}
             "Using"
             [1 2 34]
             "Kafka"
             'asdfdaf
             "to"
             :this/holy-shit
             "send"
             "this, shit!!"]]
    (>! >greet (->transit s))))

(source/stop! @source)


(comment
  (p/open)
  (p/tap)
  (tap> "Hello"))
