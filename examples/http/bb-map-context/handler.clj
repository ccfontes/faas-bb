(ns function.handler)

(defn handler [{:keys [body headers context]}]
  [(keys body) (vals body) (:content-type headers) (:upstream-url context)])
