(ns handler)

(defn handler [body headers context]
  [(keys body) (vals body) (:content-type headers) (:upstream-url context)])
