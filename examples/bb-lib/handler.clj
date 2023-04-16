(ns function.handler
  (:require [clojure.edn :as edn]
            [babashka.deps :as deps]
            [babashka.tasks]))

(deps/add-deps '{:deps {com.rpl/specter {:mvn/version "1.1.4"}}})

(require '[com.rpl.specter :refer [select]])

(defn handler [s]
  (let [v (select ["a" "b"] (edn/read-string s))]
    (if (empty? v)
      ""
      (str v))))
