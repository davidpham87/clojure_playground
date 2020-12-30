(ns hello
  (:require
   [clojure.java.io :as io]
   #_[clojure.spec.alpha :as s]
   #_[libpython-clj.python]
   #_[libpython-clj.require]))

;; (s/def ::hello string? )

(defn hello [x] (print x) x)

(println :user/name)
