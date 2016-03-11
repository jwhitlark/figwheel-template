(ns user
  (:require [figwheel-sidecar.repl-api :as ra]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.repl :refer :all]))

(defn start! [] (ra/start-figwheel!))

(defn stop! [] (ra/stop-figwheel!))

(defn cljs! [] (ra/cljs-repl "dev"))
