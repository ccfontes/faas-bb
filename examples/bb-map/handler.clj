(ns handler)

(defnk handler [body]
  [(keys body) (vals body)])
