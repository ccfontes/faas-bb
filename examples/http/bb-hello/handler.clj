(ns function.handler)

(defn handler [{:keys [body]}]
  {:body (str "Hello, " (slurp body))})
