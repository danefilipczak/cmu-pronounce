(ns pronouncing-dictionary.logic
  (:require
    [clojure.core.logic :as logic :refer [run* ==]]))


(comment

  (run* [q]
        (== q 1))

  (run* [q]
        (logic/membero q [1 2 3])
        (logic/membero q [2 3 4 5])
        (== q 2))

  (run* [q]
        (== {:a q :b 2} {:a 3 :b 2}))

  )
