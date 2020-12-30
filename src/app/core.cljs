(ns app.core
  (:require
   [reagent.dom :as dom]))

(defn app []
  [:div "Hello"])

(defn mount-app []
  (dom/render [app] (.getElementById js/document "app")))

(defn main []
  (mount-app))

(defn ^:dev/after-load reload []
  (mount-app))
