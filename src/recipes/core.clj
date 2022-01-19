(ns recipes.core
  (:require [stasis.core :as statis]
            [markdown.core :as markdown]
            [clojure.string :as str]
            [hiccup.core :refer [html]]))

(def recipes-dir "content/recipes")
(def publish-dir "out")
(def md-file-pattern #"^.*\.md$")

(defn path->html
  "Convert statis markdown key to html"
  [path]
  (str/replace path #".md" ".html"))

(defn get-markdown-content
  "Get markdown content in specified `dir`"
  [dir]
  (statis/slurp-directory dir md-file-pattern))

(defn parse-markdown
  "Parse markdown into a map where key is link and val is markdown content and metadata"
  [md-content]
  (let [paths (map path->html (keys md-content))
        html-content (map markdown/md-to-html-string-with-meta (vals md-content))]
    (zipmap paths html-content)))

(defn strip-slash
  "Strip root slash from link"
  [link]
  (str/replace link #"^/" ""))

(defn extract-links
  "Extract links from parsed content"
  [parsed-content]
  (let [hrefs       (->> parsed-content keys (map strip-slash))
        href-texts  (->> parsed-content
                         vals
                         (map #(first (get-in % [:metadata :title]))))]
    (zipmap hrefs href-texts)))

(defn wrap-with-styles
  "Wrap html content in proper style sheet"
  [html-content]
  (let [header (html [:head
                      [:link
                       {:rel "stylesheet"
                        :href "https://cdn.simplecss.org/simple.min.css"}]])]
    (str header html-content)))

(defn build-index-page
  "Build index page from links"
  [links]
  {"/index.html"
   (wrap-with-styles
    (html
     [:div
      [:h1 "Recipes"]
      [:ul
       (map (fn [[link text]]
              [:li [:a {:href link} text]]) links)]]))})

(defn build-html-pages
  "Build html pages from parsed data"
  [parsed-content]
  (reduce-kv (fn [m k v]
               (assoc m k (wrap-with-styles (:html v)))) {} parsed-content))

(defn md->html!
  "Convert Markdown in a specified `dir` to html"
  [dir]
  (let [data         (->> (get-markdown-content dir)
                          parse-markdown)
        index-page   (->> data
                          extract-links
                          build-index-page)
        html-content (->> data
                          build-html-pages)]
    (merge index-page html-content)))

(defn publish!
  "Publish markdown recipes to html"
  []
  (statis/empty-directory! publish-dir)
  (statis/export-pages (md->html! recipes-dir) publish-dir))

(defn -main
  []
  (publish!))

