(ns lein-to-deps
  (:require [clojure.string :as str]))

(defn lein->deps [xs] (into {} (for [[p v] xs] [p {:mvn/version v}])))
