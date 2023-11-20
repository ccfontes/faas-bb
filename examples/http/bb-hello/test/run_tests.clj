(ns function.test.run-tests
  (:require
    [eg :refer [eg]]
    [clojure.test :refer [run-tests]]
    [function.handler :refer [handler]]))

(eg handler
  {:body "anything"} "Hello OpenFaaS!")

(defn -main []
  (System/exit (run-tests 'function.test.run-tests))
  (let [{:keys [fail error]} (run-tests 'function.test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
