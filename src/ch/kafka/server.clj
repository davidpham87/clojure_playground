(ns ch.kafka.server
  (:require
   [camel-snake-kebab.core :as csk]
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [integrant.repl :as ir]
   [integrant.repl.state :as irs]
   [jsonista.core :as j]
   [troy-west.thimble.kafka :as ttk]
   [troy-west.thimble.zookeeper :as zookeeper])
  (:import
   (java.io File)
   (java.util Map)
   (kafka.server KafkaServerStartable KafkaConfig)
   (org.apache.kafka.clients.admin AdminClient NewTopic)))

(def object-mapper
  (j/object-mapper {:decode-key-fn csk/->kebab-case-keyword}))

(def system-state (atom {}))

(def kafka-config
  {:thimble/zookeeper.server {}

   [:thimble/kafka.broker :broker/id-0]
   {:zookeeper (ig/ref :thimble/zookeeper.server)
    :topics    ["org.sf.crime"]
    :config    {"brocker.id" "1"}}

   ;; [:thimble/kafka.broker :broker/id-1]
   ;; {:zookeeper (ig/ref :thimble/zookeeper.server)
   ;;  :topics    ["org.sf.crime"]
   ;;  :config    {"brocker.id" "2"
   ;;              "port"       "9093"}}

   :thimble/kafka.producer
   {:broker (ig/ref [:thimble/kafka.broker :broker/id-0])
    :config {"bootstrap.servers" "localhost:9092"}}

   :thimble/kafka.consumer
   {:broker (ig/ref [:thimble/kafka.broker :broker/id-0])
    :config {"group.id"           "test"
             "enable.auto.commit" "true"
             "bootstrap.servers"  "localhost:9092"}}})

(defn start
  ([]
   (start kafka-config))
  ([config]
   (ir/set-prep! (constantly config))
   (ir/prep)
   (ir/go)

   (reset! system-state irs/system)))
(defn stop
  "Stop the server"
  [state]
  (ig/halt! state))

(comment
  (start)
  (ir/set-prep! (constantly kafka-config))
  (ir/prep)
  (ir/go)
  (ir/halt)
  (ir/reset)
  (ir/clear)
  (ttk/list-topics (irs/system [:thimble/kafka.broker :broker/id-0]))
  irs/config)
