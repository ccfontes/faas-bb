(ns function.handler)

(defn handler [content]
  [(keys content) (vals content)])
