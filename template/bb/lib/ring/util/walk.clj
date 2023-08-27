(ns ring.util.walk
  ^{:author "Carlos da Cunha Fontes"
    :url "https://github.com/ccfontes/faas-bb"
    :license {:name "Distributed under the MIT License"
              :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [clojure.string :as str :refer [lower-case]]
    [clojure.walk :refer [keywordize-keys]]
    [ring.util.string :as ring-string]))

(defn read-val-strings [m]
  (into {}
    (map (fn [[k v]] [k (ring-string/read-string v)]) m)))

(defn write-val-strings [m]
  (into {}
    (map (fn [[k v]] [k (ring-string/write-string v)]) m)))

(defn lowerify-keys
  "Converts all keys in map 'm' to lowercase strings."
  [m] (into {}
        (map (fn [[k v]] [(lower-case k) v]) m)))

(defn format-context [m]
  (->> m
    (map (fn [[k v]] [(ring-string/->kebab-case k) (ring-string/read-string v)]))
    (into {})
    (keywordize-keys)))
