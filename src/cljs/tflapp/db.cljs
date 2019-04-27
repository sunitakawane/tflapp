(ns tflapp.db)

(def default-db
  {:searchterm ""
   :markers nil
   :map-ref (atom {})
   :repos []})

(defn selected-repo [repos selected]
  (some #(when (= (:id %) selected) %) repos))

