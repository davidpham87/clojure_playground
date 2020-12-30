(ns src.ch.kafka.core-light
  (:require [babashka.pods]))

(babashka.pods/load-pod ["pod-tzzh-kafka"])
(require '[pod.tzzh.kafka :as k])

(defn decode-base64-str [s]
  (when s
    (-> (java.util.Base64/getDecoder)
        (.decode s)
        String.)))

(def admin-config
  {"bootstrap.servers" "localhost:9092"})

(def admin-client
  (k/new-admin-client admin-config))

(k/get-metadata admin-client)

(k/create-topics
 admin-client
 [{:Topic "my.fourth.topics"
   :NumPartitions 1
   :ReplicationFactor 1}])

(k/delete-topics admin-client ["my.fourth.topics"])

(def producer (k/new-producer admin-config))

(def consummer
  (k/new-consumer (merge admin-config {"group.id" "kafka"
                                       "auto.offset.reset" "earliest"})))

(k/subscribe-topics consummer ["my.first.topics" "my.third.topics"])
(k/produce producer "my.first.topics" nil "hello-kafka-3")
(k/produce producer "my.third.topics" nil "hello-kafka-2")

(decode-base64-str (:Value (k/read-message consummer 100)))
