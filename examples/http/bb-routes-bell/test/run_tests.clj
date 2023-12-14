(ns test.run-tests
  (:require
    [clojure.test :refer [run-tests]]))

(defn -main []
  (let [{:keys [fail error]} (run-tests 'test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
