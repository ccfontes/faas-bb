(ns function.handler)

(defn handler [{:keys [body]}]
  [(keys body) (vals body)])
