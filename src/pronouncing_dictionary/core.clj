(ns pronouncing-dictionary.core
  (:require
    [clojure.core.logic :as l]
    [pronouncing-dictionary.db :as relations :refer [db]]
    [clojure.core.logic.pldb :as pldb]))

(defn n-similar? [n word1 word2]
  (-> (count (clojure.set/intersection (into #{}
                                             (map :sound (:phonemes word1)))
                                       (into #{}
                                             (map :sound (:phonemes word2)))))
      (>= n)))

(defn word-tailo [phonemes word-tail]
  (l/conde
    [(l/fresh [head]
       (l/firsto phonemes head)
       (l/featurec head {:stress 1})
       (l/== phonemes word-tail))]
    [(l/fresh [head tail]
       (l/conso head tail phonemes)
       (word-tailo tail word-tail))]))

(l/defne word-tailo [phonemes word-tail]
         ([[head . _] word-tail]
          (l/featurec head {:stress 1})
          (l/== phonemes word-tail))
         ([[head . tail] word-tail]
          (word-tailo tail word-tail)))


(defn rhymeso [w1 w2]
  (l/fresh [p1 p2 wt1 wt2]
    (relations/sounds-like w1 p1)
    (relations/sounds-like w2 p2)
    (word-tailo p1 wt1)
    (word-tailo p2 wt2)
    (l/== wt1 wt2)))

(comment

  (pldb/with-db
    db
    (l/run* [q]
           (rhymeso "SALVATORE" q)))

  (pldb/with-db
    db
    (l/run 1 [q]
           (l/fresh [phonemes]
             (relations/sounds-like "DANE" phonemes)
             (l/== q phonemes)))))

(comment
  ;; fun stuff to understand

  (l/defne factorialo [n m]
           ([0 1])
           ([n m]
            (l/fresh [n1 m1]
              (l/project [n]
                         (l/== n1 (- n 1)))
              (factorialo n1 m1)
              (l/project [n m1]
                         (l/== m (* n m1))))))

  (l/run 1 [q]
         (factorialo 2 q))
  )