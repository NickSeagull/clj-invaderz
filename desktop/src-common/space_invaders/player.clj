(ns space-invaders.player
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]
            [space-invaders.game-object :as game-object]))

(def ^:private sprite-width  16)
(def ^:private sprite-height 16)
(def ^:private size-scale    3)
(def ^:private player-width  (* sprite-width size-scale))
(def ^:private player-height (* sprite-height size-scale))
(def ^:private player-sprite "player.png")
(def ^:private bottom-margin 40)
(def ^:private speed         15)






(defn- middle-of [object] 
  (- 
   (/ (width object) 2)
   (/ (player-width) 2)))

(defn player? [entity]
  (= (:type entity) "Player"))

(defn- update-player [entities new-player]
  (-> (remove player? entities)
      (concat [new-player])))

(defn- out-of-left-bound [screen x]
  (< (- x speed) 0))

(defn- out-of-right-bound [screen x]
  (> (+ (+ x player-width) speed) (width screen)))

(defn- update-x [screen current-x dir]
  (case dir
    :right (if (out-of-right-bound screen current-x)
             current-x
             (+ current-x speed))
    :left  (if (out-of-left-bound screen current-x)
             current-x
             (- current-x speed))
    current-x))

(defn- overlapped-by-enemy [p entities]
  (->> (filter #(= (:type %) "Enemy") entities)
       (some #(rectangle! (:hitbox p) :overlaps (:hitbox %)))))







(defn get-player [entities]
  (first (filter player? entities)))

(defn maybe-die [entities]
  (let [p (get-player entities)]
    (if (overlapped-by-enemy p entities)
      (let [new-p (assoc p :lifes (dec (:lifes p)))]
        (sound "hit.wav" :play)
        (update-player entities new-p))
      entities)))

(defn redraw [entities]
  (update-player entities (get-player entities)))

(defn make
  [screen]
  (-> (assoc (texture player-sprite)
     	:type   "Player"
        :lifes  3
        :score  0
     	:x      (- (/ (width screen) 2) (/ player-width 2))
     	:y      bottom-margin
     	:width  player-width
     	:height player-height)
      (game-object/update-hitbox)))

(defn move
  [screen entities dir]
  (let [current-player (get-player entities)
        new-player     (-> (assoc current-player :x (update-x screen (:x current-player) dir))
                           (game-object/update-hitbox))]
    (update-player entities new-player)))
