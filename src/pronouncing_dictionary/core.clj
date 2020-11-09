(ns pronouncing-dictionary.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn parse-pronunciation [pronunciation]
  (map (fn [token]
         (let [[_ vowel stress] (re-matches #"(.*)([0-9]+)" token)]
           (merge {:sound (or vowel token)
                   :type (if vowel :vowel :consonant)}
                  (when stress {:stress (Integer/parseInt stress)}))))
       (clojure.string/split pronunciation #" ")))

(defn word-tail [phonemes]
  (drop-while (comp (partial not= 1) :stress) phonemes))

(comment

  (drop-while (comp (partial not= 1) :stress) (second (nth data 1234)))
  (word-tail (second (nth data 1234)))

  (def lines (filter (complement #(clojure.string/starts-with? % ";;;")) (clojure.string/split-lines (slurp "cmudict/cmudict-0.7b"))))

  (def data (map (fn [line] (let [[word pronunciation] (clojure.string/split line #"  ")]
                     [word (parse-pronunciation pronunciation)]))
        lines))

  (nth data 1234)

  (def rhymes-with-adoption (filter (comp
             (partial = (word-tail (second (nth data 1234))))
             word-tail
             second) data))

  (def rhymes-with-abusing (filter (comp
                                      (partial = (word-tail (second (nth data 432))))
                                      word-tail
                                      second) data))

  (map first rhymes-with-abusing)


  (def rhymes-with-abadie (filter (comp
                                    (partial = (word-tail (second (nth data 43))))
                                    word-tail
                                    second) data))

  (nth data 43)

  "([0-9]+)"
  (re-matches #"(.*)([0-9]+)" "AA0")

  )
