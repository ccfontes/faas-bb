(ns function.test.run-tests
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [function.handler :refer [handler]]))

(defn -main []
  (let [{:keys [fail error]} (run-tests 'function.test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
