(ns leiningen.new.jw-figwheel
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "jw-figwheel"))


;; Check if om or reagent are in the options
;; Copied from: https://github.com/plexus/chestnut/blob/master/src/leiningen/new/chestnut.clj

(def valid-options
  ["om" "reagent"])

(doseq [opt valid-options]
  (eval
   `(defn ~(symbol (str opt "?")) [opts#]
     (some #{~(str "--" opt)} opts#))))

(defn clean-opts
  "Takes the incoming options and compares them to the valid ones.
   It aborts the process and spits an error if an invalid option is present
   or both --om and --reagent where selected."
  [valid-options opts]
  (let [valid-opts (map (partial str "--") valid-options)]
    (doseq [opt opts]
      (if-not (some #{opt} valid-opts)
        (apply main/abort "Unrecognized option:" opt ". Should be one of" valid-opts)))
    (if (and (om? opts) (reagent? opts))
      (main/abort "Both --om and --reagent where selected. Please choose one")
      valid-opts)))

(defn jw-figwheel
  "Takes a name and options with the form --option and produces an interactive
   ClojureScript + Fighweel template.
   The valid options are:
     --om      which adds a minimal Om application in core.cljs
     --reagent which adds a minimal Reagent application in core.cljs
   Both options can't be specified at the same time. If no option is specified,
   nothing but a print statment is added in core.cljs"
  [name & opts]
  (do
    (when (= name "figwheel")
      (main/abort
       (str "Cannot name a figwheel project \"figwheel\" the namspace will clash.\n"
            "Please choose a different name, maybe \"tryfig\"?")))
    (clean-opts valid-options opts) ;; Check options for errors
    (let [data {:name name
                :sanitized (name-to-path name)
                :om? (om? opts)
                :reagent? (reagent? opts)}]
      (main/info "Generating fresh 'lein new' figwheel project.")
      (->files data
               ["README.md" (render "README.md" data)]
               ["project.clj" (render "project.clj" data)]
               ["src/{{sanitized}}/core.cljs" (render "core.cljs" data)]
               ["resources/public/index.html" (render "index.html" data)]
               ["resources/public/css/style.css" (render "style.css" data)]
               ["dev/user.clj" (render "user.clj" data)]
               [".gitignore" (render "gitignore" data)]))))
