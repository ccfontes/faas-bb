(ns handler
  (:require [bell.core :refer [router GET]]))

(def handler
  (router
    (GET "/foo/:id"
         (fn [{:keys [path-params]}]
           [(keys path-params) (vals path-params)]))))
