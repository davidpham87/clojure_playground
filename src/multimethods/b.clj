(ns multimethods.b
  (:require [multimethods.interface :refer (multi-test)]))

(defmethod multi-test :test [m]
  :b)
