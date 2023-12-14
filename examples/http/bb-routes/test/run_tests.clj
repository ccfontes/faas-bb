(ns test.run-tests
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [handler :refer [handler]]))

(eg handler
  {:request-method :get :uri "/"} "root")

(eg handler
  {:request-method :get :uri "/foo"} "foo")

(defn -main []
  (let [{:keys [fail error]} (run-tests 'test.run-tests)]
    (when (pos? (+ fail error))
      (System/exit 1))))
