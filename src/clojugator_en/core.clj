(ns clojugator-en.core
  (:require [clojugator-en.irregulars :refer [irregular-verbs]])
  (:require [clojure.string :as string])
  (:require [clj-cmudict.core :as cmu])
  (:gen-class))

(defn- final-stress [phoneme-list]
  (->> phoneme-list
       (map name)
       (string/join)
       (re-seq #"\d+")
       (last)
       (Integer.)))

(defn- always-unstressed-final?
  "Returns true if ALL pronounciations of a word have an unstressed final syllable.
  `rebel` (false) vs. `travel` (true) `blurg` (nil)."
  [verb]
  (when-let [final-stresses (seq
                              (for [entry (cmu/arpabet verb)]
                                (final-stress entry)))]
    (every? zero? final-stresses)))

(defn s-form [verb]
  (cond
    (= verb "have")                    "has"
    (re-find #"(o|ch|sh|x|s|z)$" verb) (str verb "es")
    :else                              (str verb "s")))

(defn- double-last-cons [word]
  (string/join (lazy-cat (seq word) (take-last 1 word))))

(defn- stem
  "Derive the stem to which -ed and -ing suffixes are affixed."
  [verb]
  (cond
    (re-find #"[^aiueo]e$" verb)               (string/join (drop-last verb))   ; receive -> receiv (+ed/ing)
    (.endsWith verb "ic")                      (str verb "k")                   ; panic -> panick (+ed/ing)
    (re-find #"[xwy]$" verb)                   verb                             ; say, hew, tax
    (re-find #"[^aiueo][aiue]p$" verb)         (double-last-cons verb)          ; worship, kidnap, catnap, hiccup (but not wallop, gallop)
    (always-unstressed-final? verb)            verb                             ; happen -> happen (+ed/ing) (but not rebel)
    (re-find #"[^aiueo][aiueo][^aiueo]$" verb) (double-last-cons verb)          ; trip -> tripp (+ed/ing)
    :else                                      verb))                           ; go -> go (+ed/ing)

(defn- ed-form [verb]
  (string/replace (str (stem verb) "ed") #"(ie|y)ed$" "ied"))

(defn ing-form [verb]
  (string/replace (str (stem verb) "ing") #"ieing$" "ying"))

(defn simple-past [verb]
  (:past (irregular-verbs verb) (ed-form verb)))

(defn past-participle [verb]
  (:pp (irregular-verbs verb) (ed-form verb)))

;; ============================================================================
;; the main event
;; ============================================================================

(defn conjugate
  "Return a map with various forms of the given verb. Some verbs have more than
  one realization for a given form. These are shown with pipes: got|gotten"
  [verb]
  (if (= verb "be")    ;; major exception for "be" ----------------------------
    {:plain    "be"               :ing      "being"            :pp       "been"  ; non-tensed
     :pres     "are"              :i        "am"               :s        "is"    ; non-past
     :past     "were"             :i-past   "was"              :s-past   "was"}  ; past
                       ;; all other verbs -------------------------------------
    {:plain    verb               :ing      (ing-form verb)    :pp       (past-participle verb)
     :pres     verb               :i        verb               :s        (s-form verb)
     :past     (simple-past verb) :i-past   (simple-past verb) :s-past   (simple-past verb)}))
