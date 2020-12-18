(ns pronouncing-dictionary.db
  (:require [clojure.core.logic.pldb :as pldb]))

(defn parse-pronunciation [pronunciation]
  (map (fn [token]
         (let [[_ vowel stress] (re-matches #"(.*)([0-9]+)" token)]
           (merge {:sound (or vowel token)
                   :type (if vowel :vowel :consonant)}
                  (when stress {:stress (Integer/parseInt stress)}))))
       (clojure.string/split pronunciation #" ")))

(defonce corpus (map (fn [line] (let [[word pronunciation] (clojure.string/split line #"  ")]
                                  {:word word
                                   :phonemes (parse-pronunciation pronunciation)}))
                     (filter (complement #(clojure.string/starts-with? % ";;;")) (clojure.string/split-lines (slurp "cmudict/cmudict-0.7b")))))

(pldb/db-rel ^:index word)
(pldb/db-rel ^:index phonemes)
(pldb/db-rel sounds-like ^:index word ^:index phonemes)

(def db (apply pldb/db
               (mapcat (fn [entry]
                         [[word (:word entry)]
                          [phonemes (:phonemes entry)]
                          [sounds-like (:word entry) (:phonemes entry)]]) corpus)))
