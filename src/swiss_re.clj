(ns swiss-re
  (:require [clojure.string :as str]
            [hello :as h]))

(defn key-value->map [xs]
  ;; that was what I wrote
  ;; (reduce (fn [m [k v]] (assoc m k v)) {} xs)
  ;; This is the best way
  (into {} xs))

(defn max-frequency [xs]
  (->> (into {} xs)
       vals
       frequencies
       ;; this was my solution
       ;; (reduce (fn [m [k v]] (if (> v (second m)) [k v] m)) [nil 0])
       (apply max-key second) ;, this is the best solution
       first))

(defn f [x y] 2)
