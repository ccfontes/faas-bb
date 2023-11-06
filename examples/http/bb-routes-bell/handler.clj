(ns function.handler
  (:require [bell.core :refer [router POST PUT]]))

(def handler
  (router
    (POST "/" (fn [{:keys [body]}]
                [(keys body) (vals body)]))
    (PUT "/foo/:id"
         (fn [{:keys [path-params]}]
           [(keys path-params) (vals path-params)]))))
