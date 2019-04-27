(ns tflapp.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   ;[markdown-to-hiccup.core :as m]
   [soda-ash.core :as sa]))

; (def map-ref1 (atom {}))
(def gmap (atom {}))

; (defn searchBox1 []
;   (let [searchterm (re-frame/subscribe [:searchterm])
;         loading  (re-frame/subscribe [:loading])
;         disabled (re-frame/subscribe [:fetch-searchterm-disabled])]
;       [:div
;        [sa/Search {;:type "text"
;                   :loading     @loading
;                   :disabled    @loading
;                   :value       (or @searchterm "")
;                   :placeholder "Search Location"
;                   :onResultSelect (fn [_ data]
;                                    (let [selected (js->clj (.-value data))]
;                                     (re-frame/dispatch [:repo-selected selected])))
;                   :onSearchChange #(re-frame/dispatch [:fetch-locations])
;                   :resultRenderer result-renderer
;                   :results @(re-frame/subscribe [:repos])}]]))
;                   ; :onKeyDown  (fn [e]
;                   ;               (when (and (= (.-keyCode e) 13)
;                   ;                          (not (.-shiftKey e)))
;                   ;                 (.preventDefault e)
;                   ;                 (re-frame/dispatch [:fetch-locations])))
;                   ; :onSearchChange    #(re-frame/dispatch [:searchterm-changed (-> %2
;                   ;                                                        (.-value)
;                   ;                                                        (js->clj))])}]]))
       

(defn searchBox []
  (let [searchterm (re-frame/subscribe [:searchterm])
        loading  (re-frame/subscribe [:loading])
        disabled (re-frame/subscribe [:fetch-searchterm-disabled])]
      [:div
       [sa/Input {:type "text"
                  :loading     @loading
                  :disabled    @loading
                  :value       (or @searchterm "")
                  :placeholder "Search Location"

                  :onKeyDown  (fn [e] 
                                (when (and (= (.-keyCode e) 13)
                                           (not @disabled) 
                                           (not (.-shiftKey e)))
                                  (.preventDefault e)
                                  (re-frame/dispatch [:fetch-locations])))        
                  :onChange    #(re-frame/dispatch [:searchterm-changed (-> %2
                                                                         (.-value)
                                                                         (js->clj))])}]
       [sa/Button {:primary  true
                   :loading  @loading
                   :disabled @disabled
                   :icon "search"
                   :onClick  #(re-frame/dispatch [:fetch-locations])}]]))

(defn logo []
      [:div
       [sa/Image {:src "img/TfLlogo.png"
                  :size "small"
                  :centered true
                  :circular true}]])



(defn cool-filter [[k v] l]
  (filter #(= (k %) v) l))

(defn loc-list [common-name lat lon selected id nbikes]
 [:div
  [sa/Popup {:header (str "NoOfBikes " nbikes)
             :icon "marker"
             :on "click"
             :inverted true
             :trigger (reagent/as-component [sa/ListItem {:header common-name
                                                          :icon "marker"
                                                          :value       (or (:id selected) id)
                                                          :onClick (fn [_ data]
                                                                       (let [selected (js->clj (.-value data))]
                                                                            (re-frame/dispatch [:repo-selected selected])))
                                                          :description (str lat "/" lon)}])}]])


(defn create-infowindow [id]
  (js/google.maps.InfoWindow. (clj->js {"content" (str "<div id='info-" id "'></div>")})))

(defn res-dropdown []
  (let [repos (re-frame/subscribe [:repos])
        loading   (re-frame/subscribe [:loading])
        selected  (re-frame/subscribe [:selected-repo])]
   @repos
    [:div
      [sa/ListSA
       (doall
        (for [loc @repos]
         ^{:key (:id loc)}
          [loc-list (:commonName loc) (:lat loc) (:lon loc) @selected (:id loc) (or (get (first (cool-filter [:key "NbBikes"] (loc :additionalProperties))) :value) 0)]))]]))
           

(defn m-p [m p]
  (get-in m ["properties" p]))

(defn event-info-window [event numbikes]
  [:div
   [:div  (event :commonName)]
   [:div (str " NoOfBikes : " numbikes)]])



(defn markers []
  (let [items (re-frame/subscribe [:repos])]
    @items
    (fn []
      (let [new-markers
            (doall
             (map
              (fn [item]
                (let [location  (js/google.maps.LatLng. (item :lat) (item :lon))  
                      infowindow (create-infowindow (item :id))
                      
                      numbikes (or (get (first (cool-filter [:key "NbBikes"] (item :additionalProperties))) :value) 0)
                      marker (js/google.maps.Marker. (clj->js {"position" location "map" @gmap "title" (:commonName item)}))]
                  ; (print (cool-filter [:key "NbBikes"] (item :additionalProperties)))
                  ; (print (get (first (cool-filter [:key "NbBikes"] (item :additionalProperties))) :value))
                  ; ;(print (get (item :additionalProperties) :key))
                  ;(print item)
                  (js/google.maps.event.addListener
                   marker
                   "click"
                   (fn []
                    (js/console.log "click")
                    (.open infowindow @gmap marker)))

                  (js/google.maps.event.addListener
                   infowindow
                   "domready"
                   (fn []
                    (js/console.log "domready")
                    (reagent/render (event-info-window item numbikes) (js/document.getElementById (str "info-" (item :id))))))
                  marker))
              @items))]

        (re-frame/dispatch [:set-markers new-markers])))))

(defn map-component []
 (let [
       items (re-frame/subscribe [:repos])]
  @items
   (reagent/create-class {:reagent-render (fn []
                                           [:div
                                            [:h4 "Map"]
                                            [:div  { :id "map-canvas" :style {:height "400px"}}]])
                          :component-did-mount 
                            (fn [comp]
                              (let [m-canvas  (js/document.getElementById "map-canvas")
                                    m-options (clj->js {"center" (js/google.maps.LatLng. 51.525595, -0.144083) "mapTypeId" js/google.maps.MapTypeId.ROADMAP
                                                        "zoom" 12})
                                    tgmap      (js/google.maps.Map. m-canvas m-options)]
                                   tgmap
                                  ;(js/google.maps.Marker. (clj->js {"position" (js/google.maps.LatLng. 51.525595, -0.144083), "map" tgmap}))
                                (reset! gmap tgmap)))})))
                                ; (reset! @map-ref tgmap)))}))))
                         


;; Main view
(defn main-panel []
  (let [loading   (re-frame/subscribe [:loading])
        error     (re-frame/subscribe [:error])
        repos?    (re-frame/subscribe [:has-repos])
        selected? (re-frame/subscribe [:has-selected-repo])]
    [:div#app-body
       [logo]
       [:br]
       [searchBox]
       [:br]
     [sa/Grid
      {:columns 2}
      [sa/GridColumn {:width 4}
       [res-dropdown]]
      [sa/GridColumn
       [map-component]]
      [:br]
      [markers]
      [:br]]]))
      
      

       
