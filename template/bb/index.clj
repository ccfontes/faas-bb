(ns index ^{:author "Carlos da Cunha Fontes"
            :url "https://github.com/ccfontes/faas-bb"
            :license {:name "Distributed under the MIT License"
                      :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [clojure.walk :refer [keywordize-keys]]
    [clojure.string :as str :refer [lower-case]]
    [clojure.edn :as edn]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.json :as json-middleware]
    [function.handler :as function]))

(defn read-string [s]
  (try
    (let [res (edn/read-string s)]
      (if (symbol? res) (str res) res))
    (catch Exception _
      s)))

(defn keywords? [env-val]
  (if-some [keywords (edn/read-string env-val)]
    keywords
    true))

(defn ->kebab-case [s]
  (lower-case (str/replace s #"_" "-")))

(def fn-arg-cnt (comp count first :arglists meta))

(defn format-context [m]
  (->> m
    (map (fn [[k v]] [(->kebab-case k) (read-string v)]))
    (into {})
    (keywordize-keys)))

(defn ->context [headers env]
  {:headers (format-context headers)
   :env (format-context env)})

(def response {:status 200})

(defn ->handler [f-var env]
  (let [f (var-get f-var)
        faas-fn (case (fn-arg-cnt f-var)
                  1 (comp f :body)
                  2 #(f (:body %) (->context (:headers %) env)))]
    (fn [request]
      (merge {:body (faas-fn request)} response))))

(defn ->app [f-var env]
  (-> (->handler f-var env)
    (json-middleware/wrap-json-body {:keywords? (keywords? (get env "keywords"))})
    (json-middleware/wrap-json-response)))

(defn -main []
  (run-server (->app #'function/handler (System/getenv))
              {:port 8082})
  @(promise))
