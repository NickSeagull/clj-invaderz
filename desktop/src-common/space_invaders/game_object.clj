(ns space-invaders.game-object
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]
            [space-invaders.sound-stat :as sound-stat]))

(def hitbox-margin 8)

(defn update-hitbox [entity]
  (if-not (nil? entity)
    (if-not (= (:type entity) "UI")
      (assoc entity
        :hitbox (rectangle
                 (+ (:x      entity) hitbox-margin)
                 (+ (:y      entity) hitbox-margin)
                 (- (:width  entity) hitbox-margin)
                 (- (:height entity) hitbox-margin)))
      entity)
    nil))

