#!/usr/bin/env bb

(require
  '[babashka.cli :as cli]
  '[clojure.edn :as edn]
  '[babashka.fs :as fs])

(def cli-options {:src {} :test {} :out {}})

(def edn-filenames (cli/parse-opts *command-line-args* {:spec cli-options}))

(defn merge-deps [{:keys [src test out]}]
  (spit out
    (merge
      (-> "bb.edn" slurp edn/read-string)
      (or
        (merge-with merge
          (when (fs/exists? src) (-> src slurp edn/read-string))
          (when (fs/exists? test) (-> test slurp edn/read-string)))
        {}))))

(merge-deps edn-filenames)
