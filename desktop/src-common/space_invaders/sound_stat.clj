(ns space-invaders.sound-stat
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def ^:private bottom-margin 20)
(def ^:private side-margin   50)
(def ^:private music-on      "volume-on.png")
(def ^:private music-off     "volume-off.png")
(def ^:private theme         "theme.wav")
(def ^:private pew           "lazor.wav")

(declare sound-stat?)

(defn- get-sound-stat [entities]
  (first (filter sound-stat? entities)))






(defn sound-stat? [entity]
  (= (:type entity) "SoundStat"))

(defn make [screen]
  (assoc (texture music-on)
    :type     "SoundStat"
    :x        (- (width screen) side-margin)
    :y        bottom-margin
    :width    8
    :height   8
    :bg-music (sound theme)
    :shot     (sound pew)))

(defn play-music [entities]
  (let [current-stat (get-sound-stat entities)]
    (sound! (:bg-music current-stat) :loop)))

(defn stop-music [entities]
  (let [current-stat (get-sound-stat entities)]
    (sound! (:bg-music current-stat) :stopx)))

(defn play-laser [entities]
  (let [current-stat (get-sound-stat entities)]
    (sound! (:shot current-stat) :play)))

