#!/usr/bin/env bb

(require '[function.handler :as function])

(defn read-lines
  ([] (read-lines ""))
  ([acc] (if-let [line (read-line)]
           (recur (str acc line))
           acc)))

(defn main []
  (let [input (read-lines)
        output (function/handler input)]
    (println output)))

(main)
