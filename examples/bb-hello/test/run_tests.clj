(ns test.run-tests
  (:require
    [eg :refer [eg]]
    [clojure.test :refer [run-tests]]
    [handler :refer [handler]]))

(eg handler
  {:body "anything"} "Hello OpenFaaS!")

(defn -main []
  (let [{:keys [fail error]} (run-tests 'test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
