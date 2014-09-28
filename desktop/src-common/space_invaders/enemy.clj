(ns space-invaders.enemy
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]
            [space-invaders.game-object :as game-object]
            [space-invaders.player :as player]))

(def ^:private enemy-sprite   "enemy.png")
(def ^:private sprite-height  16)
(def ^:private sprite-width   16)
(def ^:private size-scale     4)
(def ^:private enemy-height   (* sprite-height size-scale))
(def ^:private enemy-width    (* sprite-width size-scale))
(def ^:private speed          18)

(defn- random-x [screen]
  (int (rand (width screen))))

(defn- get-angle [enemy entities]
  (let [ex (:x enemy)
        ey (:y enemy)
        px (:x (player/get-player entities))
        py (:y (player/get-player entities))]
    (assoc enemy
      :angle (int (Math/toDegrees (Math/atan (/ (- px ex) (- ey py))))))))

(defn- enemy? [entity]
  (= (:type entity) "Enemy"))

(defn- out-of-bounds [entity]
  (< (:y entity) 0))

(defn- move-enemy [entity]
  (if (enemy? entity) 
    (if-not (out-of-bounds entity) 
      (let [a (Math/toRadians (:angle entity))
            x (+ (:x entity) (* (Math/sin a) speed))
            y (- (:y entity) (* (Math/cos a) speed))]
        (assoc entity
          :x x
          :y y))
      nil)
    entity))

(defn- overlapped-with-bullet? [entity entities]
  (->> (filter #(= (:type %) "Bullet") entities)
       (some #(rectangle! (:hitbox entity) :overlaps (:hitbox %)))))

(defn- overlapped-by-player [entity entities]
  (->> (filter #(= (:type %) "Player") entities)
       (some #(rectangle! (:hitbox entity) :overlaps (:hitbox %)))))

(defn- maybe-die [entity entities]
  (if (enemy? entity)
    (if (or 
         (overlapped-with-bullet? entity entities) 
         (overlapped-by-player entity entities))
      nil
      entity)
    entity))

(defn update-player-score [entity entities]
  (if (player/player? entity)
    (if (some 
         #(overlapped-with-bullet? % entities) 
         (filter #(= (:type %) "Enemy") entities))
      (let [p (player/get-player entities)]
        (if (and (= (mod (:score p) 1000) 0) (not (zero? (:score p))))
          (do
            (sound "1up.wav" :play)
            (assoc p 
              :score (+ (:score p) 50)
              :lifes (inc (:lifes p))))
          (assoc p
            :score (+ (:score p) 50))))
      entity)
    entity))

(defn make [screen entities]
  (-> (assoc (texture enemy-sprite)
        :type   "Enemy"
        :x      (random-x screen)
        :y      (height screen)
        :width  enemy-width
        :height enemy-height)
      (get-angle entities)
      (game-object/update-hitbox)))

(defn move-all [entities]
  (->> (map move-enemy entities)
       (map #(update-player-score % entities))
       (map #(maybe-die % entities))
       (map game-object/update-hitbox)))
