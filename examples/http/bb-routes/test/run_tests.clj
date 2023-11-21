(ns function.test.run-tests
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [function.handler :refer [handler]]))

(eg handler
  {:request-method :get :uri "/"} "root")

(eg handler
  {:request-method :get :uri "/foo"} "foo")

(defn -main []
  (let [{:keys [fail error]} (run-tests 'function.test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
