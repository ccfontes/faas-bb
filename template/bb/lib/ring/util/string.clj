(ns ring.util.string
  (:require
    [clojure.string :as str :refer [lower-case]]
    [clojure.edn :as edn]))

(defn ->kebab-case [s]
  (lower-case (str/replace s #"_" "-")))

(defn read-string [s]
  (try
    (let [res (edn/read-string s)]
      (if (or (symbol? res) (keyword? res))
        s
        res))
    (catch Exception _
      s)))

(defn write-string [x]
  (if (or (symbol? x) (name x))
    (name x)
    (str x)))
