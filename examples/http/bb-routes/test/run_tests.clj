(ns test.run-tests
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [handler :refer [handler]]))

(eg handler
  {:uri "/" :request-method :get} "root")

(eg handler
  {:uri "/foo" :request-method :get} "foo")

(defn -main []
  (let [{:keys [fail error]} (run-tests 'test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
