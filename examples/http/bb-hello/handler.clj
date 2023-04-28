(ns function.handler)

(defn handler [{:keys [body] :as req}]
  {:body (str "Hello, " (slurp body))})
