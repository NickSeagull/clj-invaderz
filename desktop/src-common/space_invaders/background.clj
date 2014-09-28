(ns space-invaders.background
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(def ^:private bg "bg.png")
(def ^:private speed 3)



(defn- background? [entity]
  (= (:type entity) "Background"))

(defn- out-of-screen [entity screen]
  (< (:y entity) (- 0 (height screen))))

(defn- move-to-top [entity screen]
  (assoc entity :y (- (height screen) 10)))

(defn- move-down [entity]
  (assoc entity :y (- (:y entity) speed)))

(defn- move-background [entity screen]
  (if (background? entity)
    (if (out-of-screen entity screen)
      (move-to-top entity screen)
      (move-down entity))
    entity))

(defn make [screen y]
  (assoc (texture bg)
    :type "Background"
    :x 0
    :y y
    :width (width screen)
    :height (+ (height screen) 10)))

(defn move-all [entities screen]
  (map #(move-background % screen) entities))
