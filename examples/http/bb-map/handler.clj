(ns function.handler)

(defn handler [content {:keys [headers env]}]
  [content (:content-type headers) (:keywords env)])
