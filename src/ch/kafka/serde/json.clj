(ns ch.kafka.json
  "Implements a JSON SerDes (Serializer/Deserializer)."
  {:license "BSD 3-Clause License <https://github.com/FundingCircle/jackdaw/blob/master/LICENSE>"}
  (:require [jsonista.core :as j]
            [jackdaw.serdes.fn :as jsfn])
  (:import java.nio.charset.StandardCharsets
           org.apache.kafka.common.serialization.Serdes))

(set! *warn-on-reflection* true)

(defn to-bytes
  "Converts a string to a byte array."
  [data]
  (.getBytes ^String data StandardCharsets/UTF_8))

(defn from-bytes
  "Converts a byte array to a string."
  [^bytes data]
  (String. data StandardCharsets/UTF_8))

(defn serializer
  "Returns a JSON serializer."
  []
  (jsfn/new-serializer
   {:serialize (fn [_ _ data] (when data (j/write-value-as-bytes data)))}))

(def object-mapper (j/object-mapper {:decode-key-fn keyword}))

(defn deserializer
  "Returns a JSON deserializer."
  []
  (jsfn/new-deserializer
   {:deserialize
    (fn [_ _ data] (when data (-> (from-bytes data)
                                  (j/read-value object-mapper))))}))

(defn serde
  "Returns a JSON serde."
  []
  (Serdes/serdeFrom (serializer) (deserializer)))
