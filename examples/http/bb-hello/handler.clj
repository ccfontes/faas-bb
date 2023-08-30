(ns function.handler)

(defn handler [{:keys [body]}]
  (println "Hello, " body)
  {:body (str "Hello, " body)})
