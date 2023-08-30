(ns ring.middleware.text)

(defn text-request? [request]
  (when-let [type (get-in request [:headers "content-type"])]
    (seq (re-find #"^text/plain" type))))

(defn wrap-text-body [handler]
  (fn [request]
    (if (text-request? request)
      (handler (update request :body slurp))
      (handler request))))
