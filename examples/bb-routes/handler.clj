(ns handler)

(defnk handler [request-method uri]
  (case [request-method uri]
    [:get "/"] "root"
    [:get "/foo"] "foo"))
