(ns function.handler)

(defn handler [content {:keys [headers env]}]
  [(keys content) (vals content) (:content-type headers) (:keywords env)])
