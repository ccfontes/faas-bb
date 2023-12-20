(ns handler
  (:require view))

(defnk handler [body]
  (view/render-page "My Page" body))
