(ns ring.middleware.text
  ;^{:author "Carlos da Cunha Fontes"
  ;:url "https://github.com/ccfontes/faas-bb"
  ;:license {:name "Distributed under the MIT License"
  ;          :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
)

(defn text-request? [request]
  (boolean
    (when-let [type (get-in request [:headers "content-type"])]
      (seq (re-find #"^text/plain" type)))))

(defn wrap-text-body [handler]
  (fn [request]
    (if (text-request? request)
      (handler (update request :body slurp))
      (handler request))))
