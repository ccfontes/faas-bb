(ns function.handler)

(defn handler [{:keys [request-method uri]}]
  (case [request-method uri]
    [:get "/"] (constantly "root")
    [:get "/foo"] (constantly "foo")))
