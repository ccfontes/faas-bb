#!/usr/bin/env bb

(require
  '[clojure.test :as test :refer [deftest is]]
  '[function.handler :refer [handler]])

(deftest handler-test
  (is (= "" (handler "{\"a\" 10}")))
  (is (= "[10]" (handler "{\"a\" {\"b\" 10}}"))))

(defn run-tests []
   (let [{:keys [fail error]} (test/run-tests)]
     (when (and fail error (pos? (+ fail error)))
       (System/exit 4))))

(run-tests)
