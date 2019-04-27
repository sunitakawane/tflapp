(defproject tflapp "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [soda-ash "0.83.0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [ring "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler tflapp.handler/dev-handler}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]

    :plugins      [[lein-figwheel "0.5.18"]]}
   :prod {}
   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  true
             :main         tflapp.server
             :aot          [tflapp.server]
             :uberjar-name "tflapp.jar"
             :prep-tasks   ["compile" ["cljsbuild" "once" "min"]]}}
   

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "tflapp.core/mount-root"}
     :compiler     {:main                 tflapp.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}
                    

    {:id           "min"
     :source-paths ["src/cljs"]
     :jar true
     :compiler     {:main            tflapp.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})


    
  
