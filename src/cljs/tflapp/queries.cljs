(ns tflapp.queries
  (:require [tflapp.db :as db]
            [clojure.string :as str]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :loading
 (fn [db]
   (:loading db)))

(re-frame/reg-sub
 :searchterm
 (fn [db]
   (:searchterm db)))

(re-frame/reg-sub
 :markers
 (fn [db]
   (:markers db)))

(re-frame/reg-sub
 :fetch-searchterm-disabled
 (fn [_ _]
   [(re-frame/subscribe [:searchterm])
    (re-frame/subscribe [:loading])])
 (fn [[searchterm loading] _]
   (or (str/blank? searchterm)
       loading)))

(re-frame/reg-sub
 :error
 (fn [db]
   (:error db)))

(re-frame/reg-sub
 :repos
 (fn [db]
   (:repos db)))

(re-frame/reg-sub
 :map-ref
 (fn [db]
   (:map-ref db)))

(re-frame/reg-sub
 :has-repos
 (fn [_]
   (re-frame/subscribe [:repos]))
 (fn [repos]
   (boolean (seq repos))))

(re-frame/reg-sub
  :selected-repo
  (fn [db]
      (:selected-repo db)))

(re-frame/reg-sub
  :has-selected-repo
  (fn [_]
      (re-frame/subscribe [:selected-repo]))
  (fn [repo]
      (some? repo)))