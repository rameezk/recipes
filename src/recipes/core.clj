(ns recipes.core
  (:require [stasis.core :as statis]
            [markdown.core :as markdown]
            [clojure.string :as str]))

(def recipes-dir "content/recipes")
(def publish-dir "out")
(def md-file-pattern #"^.*\.md$")

(defn key->html
  "Convert statis markdown key to html"
  [k]
  (str/replace k #".md" ".html"))

(defn read-and-convert!
  "Read and convert markdown files to html"
  []
  (let [md-files       (statis/slurp-directory recipes-dir md-file-pattern)
        paths          (map key->html (keys md-files))
        html-content   (map markdown/md-to-html-string (vals md-files))]
    (zipmap paths html-content)))

(statis/empty-directory! publish-dir)
(statis/export-pages (read-and-convert!) publish-dir)

(comment
  (read-and-convert!)

  )



