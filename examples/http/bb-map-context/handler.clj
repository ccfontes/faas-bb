(ns function.handler)

(defn handler [{:keys [body headers env]}]
  [(keys body) (vals body) (:content-type headers) (:upstream-url env)])
