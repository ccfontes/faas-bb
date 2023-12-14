(ns handler
  (:require view))

(defn handler [{:keys [body]}]
  (view/render-page "My Page" body))
