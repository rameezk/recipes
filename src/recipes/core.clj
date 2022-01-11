(ns recipes.core
  (:require [stasis.core :as statis]))

(def recipes-dir "content/recipes")

(statis/slurp-directory recipes-dir #".*\.md$")
;; => {"/neapolitan-pizza.md" "## Ingredients\n- 00 Flour\n\n## Method\n### Day 1\n- Make Poolish\n"}
