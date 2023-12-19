(ns view
  (:require [hiccup2.core :refer [html]]))

(defn ->item-list [lst]
  [:ul (for [x lst] [:li x])])

(defn hiccup-page [title {:keys [item-list]}]
  [:html
    [:head
      [:title title]]
    [:body
      [:h1 title]
      [:div {:id "item-list"}
        (->item-list item-list)]]])

(defn render-page [title data]
  (-> (hiccup-page title data) html str))
