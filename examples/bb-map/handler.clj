(ns handler)

(defn handler [{:keys [body]}]
  [(keys body) (vals body)])
