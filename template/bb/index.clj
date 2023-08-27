(ns index ^{:author "Carlos da Cunha Fontes"
            :url "https://github.com/ccfontes/faas-bb"
            :license {:name "Distributed under the MIT License"
                      :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.json :refer [wrap-json-body]]
    [ring.middleware.headers :refer [wrap-lowercase-headers wrap-friendly-headers]]
    [ring.middleware.headers]
    [ring.util.walk :as ring-walk]
    [compojure.response :as response]
    [function.handler :as function]))

(def keywords? #(if (nil? %) true %))

(defn ->handler [f env]
  (fn [request]
    (response/render f
      (assoc request :context env))))

(defn ->app [f env]
  (-> (->handler f env)
    (wrap-friendly-headers)
    (wrap-json-body {:keywords? (-> env :keywords keywords?)})
    (wrap-lowercase-headers)))

(defn -main []
  (let [env (ring-walk/format-context (System/getenv))]
    (run-server (->app #'function/handler env)
                {:port 8082})
    @(promise)))
