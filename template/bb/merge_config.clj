(ns merge-config
  (:require
    [babashka.cli :as cli]
    [clojure.edn :as edn]
    [babashka.fs :as fs]))

(def cli-options {:src {} :test {} :out {}})

(defn -main [& cli-args]
  (let [{:keys [src test out]} (cli/parse-opts cli-args {:spec cli-options})]
    (spit out
      (merge-with merge
        (-> "bb.edn" slurp edn/read-string)
        (or
          (merge-with merge
            (when (fs/exists? src) (-> src slurp edn/read-string (select-keys [:deps])))
            (when (fs/exists? test) (-> test slurp edn/read-string (select-keys [:deps]))))
          {})))))
