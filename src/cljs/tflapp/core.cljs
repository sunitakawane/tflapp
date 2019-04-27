(ns tflapp.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [tflapp.events]
   [tflapp.queries]
   [tflapp.views :as views]
   [tflapp.config :as config]))
   


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn home-render []
  [:div {:style {:height "300px"}}])
   

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
