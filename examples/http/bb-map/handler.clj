(ns function.handler)

(defn handler [content context]
  (println "content" content)
  (println "context" context)
  (update content :bar str "spam"))
