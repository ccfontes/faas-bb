(ns function.handler
  (:require [babashka.process :as process]
            [clojure.data.json :as json]
            [monger.core :as mg]
            [monger.collection :as mc]))

(def clients-db (atom nil))

(defn prepare-db []
  (let [url (str "mongodb://" (System/getenv "mongo") ":27017/clients")]
    (if @clients-db
      @clients-db
      (do
        (reset! clients-db (mg/get-db (mg/connect url) "clients"))
        @clients-db))))

(defn insert-user [event context]
  (let [users (prepare-db)
        record {:name (:body event)}]
    (try
      (mc/insert users "users" record)
      (context
        :status 200
        :succeed {:status (str "Insert done of: " (json/write-str (:body event)))})
      (catch Exception e
        (context :fail (.toString e))))))

(defn handler [event context]
  (insert-user event context))

;; ------------------------

(defn handler [s])