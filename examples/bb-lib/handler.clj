(ns function.handler
  (:require [clojure.edn :as edn]
            [com.rpl.specter :refer [select]]))

(defn handler [s]
  (let [v (select ["a" "b"] (edn/read-string s))]
    (if (= v [nil])
      ""
      (str v))))
