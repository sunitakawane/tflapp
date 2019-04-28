(ns tflapp.events
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [day8.re-frame.http-fx] ;; even if we don't use this require its existence will cause the :http-xhrio effect handler to self-register with re-frame
   [ajax.core :as ajax]
   [clojure.string :as str]
   ;[tflapp.views :as views]
   [tflapp.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :searchterm-changed
 (fn [db [_ searchterm]]
  (let [current-markers (-> db :markerlist)]
     (when-not (empty? current-markers)
       (doall (map #(js-invoke % "setMap" nil)
                   current-markers)))
   (-> db
       (assoc :searchterm searchterm)
       (dissoc :markerlist)
       (dissoc :repos)))))

(re-frame/reg-event-db
 :set-markers
 (fn [db [_ new-markers]]
   (let [current-markers (-> db :markerlist)]
     (when-not (empty? current-markers)
       (doall (map #(js-invoke % "setMap" nil)
                   current-markers))))
   (assoc-in db [:markerlist] new-markers)))

(re-frame/reg-event-db
 :repos-loaded
 (fn [db [_ response]]
   (-> db
       (dissoc :loading :error)
       (assoc :repos response))))

(re-frame/reg-event-db
 :ajax-failed
 (fn [db [_ error]]
   (-> db
       (dissoc :loading)
       (dissoc :repos)
       (assoc :error error))))

(re-frame/reg-event-fx
 :fetch-locations
 (fn [{db :db} _]
   (let [searchterm (:searchterm db)]
     {:db         (-> db
                      (assoc :loading true)
                      (dissoc :error))
      :http-xhrio {:method          :get
                   :uri             (if searchterm (str "https://api.tfl.gov.uk/Place/Search?name=" searchterm "&types=BikePoint&app_id=d6b3fe7c&app_key=4ae6aee53e98571d8c0e9e8d9b04aa66") "")

                   :timeout         5000
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:repos-loaded]
                   :on-failure      [:ajax-failed]}})))


(re-frame/reg-event-fx
 :repo-selected
 (fn [{db :db} [_ selected]]
     (let [repo (db/selected-repo (:repos db) selected)
           ;map-canvas (reagent/dom-node this)
           ;map-options (clj->js {"center" (js/google.maps.LatLng. -34.397, 150.644)
                                ;  "zoom" 8
           ;marker (create-marker (:id repo) (:lat repo) (:lon repo))
          ;  infowindow (create-infowindow (:id repo))
           latlon (:lat repo)]
          
          (println  (:id repo)))))
 ;  (let [repo (db/selected-repo (:repos db) selected)
 ;        ;uri  (str/replace (:trees_url repo) "{/sha}" "/HEAD")
 ;        ]
 ;    (assert (= selected (:id repo)) (str "Mismatching repo:" (pr-str repo)))
 ;    {:db         (-> db
 ;                     (assoc :selected-repo repo)
 ;                     (assoc :loading true)
 ;                     (dissoc :tree :error))
 ;     :http-xhrio {:method          :get
 ;                  ;:uri             uri
 ;                  :timeout         10000
 ;                  :response-format (ajax/json-response-format {:keywords? true})
 ;                  :on-success      [:tree-loaded]
 ;                  :on-failure      [:ajax-failed]}})

 

(re-frame/reg-event-db
 :tree-loaded
 (fn [db [_ response]]
   (-> db
       (dissoc :loading :error)
       (assoc :tree response))))
