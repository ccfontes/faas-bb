(ns function.handler)

(defn handler [content {:keys [headers env] :as context}]
  (println "context" context)
  [(keys content) (vals content) (:content-type headers) (:keywords env)])
