(ns multimethods.c
  (:require [multimethods.interface :refer (multi-test)]))

(defmethod multi-test :test [m]
  :c)
