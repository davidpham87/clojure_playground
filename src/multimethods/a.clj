(ns multimethods.a
  "Testing how multimethods works"
  (:require
   [multimethods.interface :refer (multi-test)]
   [multimethods.b]
   ;; if you comment c and reload it should :b
   [multimethods.c]))

#_(defmethod multi-test :test [_] :a)

(multi-test {:type :test}) ;; should be :c
