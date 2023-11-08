(ns function.handler
  (:require [function.view :as view]))

(defn handler [{:keys [body]}]
  (view/render-page "My Page" body))
