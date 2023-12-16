(ns index
  ^{:author "Carlos da Cunha Fontes"
    :url "https://github.com/ccfontes/faas-bb"
    :license {:name "Distributed under the MIT License"
              :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.json :refer [wrap-json-body]]
    [ring.middleware.text :refer [wrap-text-body]]
    [ring.middleware.headers :refer [wrap-lowercase-headers wrap-friendly-headers]]
    [ring.util.walk :as ring-walk]
    [compojure.response :as response]
    [handler :as function]))

(def keywords? #(if (nil? %) true %))

(def fn-arg-cnt (comp count first :arglists meta))

(defn wrap-arg [f-var]
  (let [f (var-get f-var)]
    (case (fn-arg-cnt f-var)
      0 (fn [_] (f))
      1 f)))

(defn ->handler [f env]
  (fn [request]
    (response/render f
      (assoc request :context env))))

(defn ->app [f env]
  (-> (->handler f env)
    (wrap-friendly-headers)
    (wrap-text-body)
    (wrap-json-body {:keywords? (-> env :keywords keywords?)})
    (wrap-lowercase-headers)))

(defn -main []
  (let [env (ring-walk/format-context (System/getenv))
        faas-fn (wrap-arg #'function/handler)]
    (run-server (->app faas-fn env)
                {:port 8082})
    @(promise)))
