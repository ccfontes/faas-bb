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
    [ring.util.string :as ring-string]
    [compojure.response :as response]
    [babashka.fs :as fs]
    [clojure.string :as str]
    [clojure.edn :as edn]
    [handler :as function]))

(defn ->secret [filepath secret-raw]
  (let [secret (edn/read-string secret-raw)]
    (if (map? secret)
      secret
      (let [secret-key (-> filepath fs/file-name keyword)]
        {secret-key (ring-string/read-string secret-raw)}))))

(defn ->secrets []
  (when (fs/exists? "/var/openfaas/secrets")
    (->> (fs/list-dir "/var/openfaas/secrets")
      (map #(let [filepath (-> % str)
                  secret-raw (slurp filepath)]
             (->secret filepath secret-raw)))
      (apply merge))))

(def keywords? #(if (nil? %) true %))

(def fn-arg-cnt #(some-> % meta :arglists first count))

(defn wrap-arg [f-var]
  (let [f (var-get f-var)]
    (case (fn-arg-cnt f-var)
      0 (fn [_] (f))
      f)))

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
        context (merge env (->secrets))
        faas-fn (wrap-arg #'function/handler)]
    (run-server (->app faas-fn context)
                {:port 8082})
    @(promise)))
