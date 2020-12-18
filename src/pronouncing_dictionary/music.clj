(ns pronouncing-dictionary.music
  (:require [clojure.core.logic :as l]
            [clojure.core.logic.fd :as fd]))


;; code for this blog post https://wickstrom.tech/generative-music/2016/08/07/generating-sight-reading-exercises-using-constraint-logic-programming-in-clojure-part-1.html

;; other core.logic counterpoint generation https://github.com/namin/metasolfeggio/blob/master/overtone/counterpoint.clj

;; dissertation on using mini-kanren (python host) for canon generation http://minikanren.org/fox_diss.pdf


(defn pitcho [p]
  (fd/in p (fd/interval 1 7)))

(defn note-valueo [v]
  (fd/in v (fd/domain 1 2 4 8 16)))

(l/defne noteo [note]
  ([[p v]]
   (pitcho p)
   (note-valueo v)))

(l/defne noteso [notes]
  ([notes]
   (l/emptyo notes))
  ([[n . ns]]
   (noteo n)
   (noteso ns)))

(defn psuedo-noteso [notes] ;; everyg is psuedo-relational... it can't be run backwards
  (l/everyg noteo notes))


;; analogous to (reduce + (map first notes))
(l/defne notes-total-valueo [notes total]
  ([[] _]
   (fd/== total 0))
  ([[[p v] . ns] _]
   (l/fresh [sum]
     (note-valueo v)
     (fd/+ v sum total)
     (notes-total-valueo ns sum))))

(defn baro [notes]
  (l/fresh []
    (noteso notes)
    (notes-total-valueo notes 16)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; convenience functions

(defn ->pitch [p]
  (nth [:c :d :e :f :g :a :b] (- p 1)))

(defn ->note-value [d]
  (/ d 16))

(defn ->note [[p d]]
  [(->pitch p)
   (->note-value d)])

(defn ->bar [bar]
  (map ->note bar))


(comment
  (map ->bar (l/run 10 [q]
                     (baro q)))

  (l/run 5 [q]
         (noteso [[1 4]])
         (l/== q "true")))