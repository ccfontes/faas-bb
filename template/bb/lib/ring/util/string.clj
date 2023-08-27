(ns ring.util.string
  ^{:author "Carlos da Cunha Fontes"
    :url "https://github.com/ccfontes/faas-bb"
    :license {:name "Distributed under the MIT License"
              :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
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
  (if (or (symbol? x) (keyword? x))
    (name x)
    (str x)))
