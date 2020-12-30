(ns datahike-test
  (:require [datahike.api :as d]))

(def cfg {:store {:backend :mem}
          :schema-flexibility :write
          :keep-history?      true})

(def schema
  [#:db{:ident       :user/name
        :valueType   :db.type/string
        :cardinality :db.cardinality/one
        :unique      :db.unique/identity
        :doc         "User name"}

   #:db{:ident       :user/age
        :valueType   :db.type/long
        :cardinality :db.cardinality/one
        :doc         "User age"}

   #:db{:ident       :user/friends
        :valueType   :db.type/ref
        :cardinality :db.cardinality/many
        :doc         "Friends"}])

(def conn (atom nil))

(d/delete-database cfg)
(d/create-database cfg)
(let [connection (d/connect cfg)]
  (d/transact connection schema)
  (reset! conn @connection))

(def tx-data [{:userr/name "Andrew"
               :db/id "abcd"
               :user/age 20}
              {:user/name "Barbara"
               :user/age 50
               :user/friends [{:db/id "abcde"}]}])

(d/transact conn tx-data)

(d/datoms @conn :eavt)
