(ns function.handler)

(defn app [{:keys [request-method uri]}]
  (case [request-method uri]
    [:get "/"] (constantly "root")
    [:get "/foo"] (constantly "foo")))
