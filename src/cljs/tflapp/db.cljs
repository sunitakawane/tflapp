(ns tflapp.db)

(def default-db
  {:searchterm ""
   :markerlist nil
   :map-ref (atom {})
   :repos nil})

(defn selected-repo [repos selected]
  (some #(when (= (:id %) selected) %) repos))

