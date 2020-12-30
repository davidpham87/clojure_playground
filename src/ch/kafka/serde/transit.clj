(ns ch.kafka.serde.transit
  "Implements a transit SerDes (Serializer/Deserializer)."
  {:license "BSD 3-Clause License <https://github.com/FundingCircle/jackdaw/blob/master/LICENSE>"}
  (:require [clojure.core.rrb-vector]
            [clojure.java.io]
            [cognitect.transit :as t]
            [jackdaw.serdes.fn :as jsfn]
            [jsonista.core :as j])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           org.apache.kafka.common.serialization.Serdes))

(set! *warn-on-reflection* true)

(defn serializer
  "Returns a transit serializer."
  [{:keys [type] :or {type :json}}]
  (let [writer (t/writer (ByteArrayOutputStream. 4096) type)]
    (jsfn/new-serializer
     {:serialize
      (fn [_ _ data] (when data (-> (t/write writer data) j/write-value-as-bytes)))})))

(defn deserializer
  "Returns a transit deserializer."
  [{:keys [type] :or {type :json}}]
  (jsfn/new-deserializer
   {:deserialize
    (fn [_ _ data]
      (when data
        (-> (t/reader (ByteArrayInputStream. data))
            (t/read type))))}))

(defn serde
  "Returns a transit serde."
  ([] (serde {:type :json}))
  ([{:keys [type] :as m}]
   (Serdes/serdeFrom (serializer m) (deserializer m))))

(comment

  (import java.nio.charset.StandardCharsets)
  (defn to-bytes
    "Converts a string to a byte array."
    [data]
    (.getBytes ^String data StandardCharsets/UTF_8))

  (defn from-bytes
    "Converts a byte array to a string."
    [^bytes data]
    (String. data StandardCharsets/UTF_8))

  (let [baos (ByteArrayOutputStream.)
        _ (t/write (t/writer baos :json) {:a 3 :b 3})
        bais (ByteArrayInputStream. (.toByteArray baos))]
    (t/read (t/reader bais :json)))

  (def baos (ByteArrayOutputStream.))
  (t/write (t/writer baos :json) {:a 3 :b 3})
  (def s (.toString baos))
  (t/read (t/reader (ByteArrayInputStream. (to-bytes s)) :json)))
