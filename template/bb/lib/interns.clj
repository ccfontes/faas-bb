(ns interns
  (:require [plumbing.core]))

(intern 'clojure.core
        (with-meta 'defnk {:macro true})
        @#'plumbing.core/defnk)
