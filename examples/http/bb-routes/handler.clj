(ns function.handler)

(defn handler [{:keys [request-method uri]}]
  (case [request-method uri]
    [:get "/"] "root"
    [:get "/foo"] "foo"))
