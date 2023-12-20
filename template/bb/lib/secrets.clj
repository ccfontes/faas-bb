(ns secrets
  (:require
    [ring.util.string :as ring-string]
    [babashka.fs :as fs]
    [clojure.edn :as edn]))

(def secrets-dir "/var/openfaas/secrets")

(defn ->secret [filepath secret-raw]
  (let [secret (edn/read-string secret-raw)]
    (if (map? secret)
      secret
      (let [secret-key (-> filepath fs/file-name keyword)]
        {secret-key (ring-string/read-string secret-raw)}))))

(defn ->secrets []
  (when (fs/exists? secrets-dir)
    (->> (fs/list-dir secrets-dir)
      (map #(let [filepath (-> % str)
                  secret-raw (slurp filepath)]
             (->secret filepath secret-raw)))
      (apply merge))))
