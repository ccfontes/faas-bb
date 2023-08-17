(ns index
  (:require
    [function.handler :as function]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.json :as json-middleware]
    ;[spy.core :refer [spy]]
    [clojure.walk :refer [keywordize-keys]]
    [clojure.string :as str :refer [lower-case]]
    [clojure.edn :as edn]))

(defn read-string [s]
  (try (edn/read-string s)
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

(defn ->context [{:keys [headers]} env]
  {:headers (format-context headers)
   :env (format-context env)})

(def response {:status 200})

(defn ->handler [f env]
  (fn [request]
    (let [faas-fn (case (fn-arg-cnt f)
                    1 (comp function/handler :body)
                    2 #(function/handler (:body %)
                                         (->context (:headers %) env)))]
      (println "request" request)
      ; TODO replace {} with request, but need to remove troublesome keys
      (merge (assoc {} :body (faas-fn request))
             response))))

(defn ->app [f env]
  (-> (->handler f env)
    (json-middleware/wrap-json-body {:keywords? (keywords? (get env "keywords"))})
    (json-middleware/wrap-json-response)))

(defn -main []
  (run-server (->app #'function/handler (System/getenv))
              {:port 8082})
  @(promise))
