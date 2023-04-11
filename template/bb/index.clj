#!/usr/bin/env bb

(require '[clojure.java.io :as io])
(require '[function.handler :as function])

(defn read-lines
  ([] (read-lines ""))
  ([acc] (let [line (read-line)]
           (if line
             (recur (str acc line))
             acc))))

(defn main []
  (let [input (read-lines)
        output (function/handler input)]
    (println output)))

(main)
