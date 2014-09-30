(ns space-invaders.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [space-invaders.main-level :as main-level]
            [space-invaders.sound-stat :as sound-stat]
            [space-invaders.player :as player]
            [clojure.java.io :as io]))

(declare space-invaders main-level-screen game-over-screen)

(defn game-over []
  (set-screen! space-invaders game-over-screen))

(defn save-score [score]
  (spit "score.temp" (str score)))

(defn get-score []
  (let [score (slurp "score.temp")]
    (io/delete-file "score.temp")
    score))

(defscreen main-level-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (main-level/start screen entities))

  :on-render
  (fn [screen entities]
    (clear!)
    (let [new-entities (main-level/render screen entities)
          p (player/get-player new-entities)]
      (if (= (:lifes p) 0)
        (do 
          (save-score (:score p))
          (game-over))
        (render! screen new-entities))))

  :on-key-down
  (fn [screen entities]
    (cond
     (key-pressed? :r)
     (on-gl (set-screen! space-invaders main-level-screen))
     (key-pressed? :space)
     (let [lazor (sound "lazor.wav" :play)]
       (main-level/make-bullet screen entities) )))


  :on-resize
  (fn [screen entities]
    (height! screen 600))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :spawn-enemy (main-level/spawn-enemy screen entities))))






(defscreen title-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [bg (assoc (texture "titlebg.png")
               :type "Background"
               :width (width screen)
               :height (height screen))
          title (assoc (texture "title.png")
                  :type "Title"
                  :width (* 64 8)
                  :height (* 16 8)
                  :x (- (/ (width screen) 2) (/ (* 64 8) 2))
                  :y (- (height screen) 250))
          msg (assoc (texture "pressret.png")
                :type "Message"
                :width (* 32 8)
                :height (* 4 8)
                :x (- (/ (width screen) 2) (/ (* 32 8) 2))
                :y (- (height screen) 400))]
      [bg title msg]))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
     (key-pressed? :enter)
     (on-gl (set-screen! space-invaders main-level-screen))))


  :on-resize
  (fn [screen entities]
    (height! screen 600)))


(defscreen game-over-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [bg (assoc (texture "titlebg.png")
               :type "Background"
               :width (width screen)
               :height (height screen))
          title (assoc (texture "broken.png")
                  :type "Title"
                  :width (width screen)
                  :height (width screen))
          msg (assoc (texture "ufail-png.png")
                :type "Message"
                :width (* 47 8)
                :height (* 16 8)
                :x (- (/ (width screen) 2) (/ (* 47 8) 2))
                :y (- (height screen) 200))
          ret (assoc (texture "pressret.png")
                :width (* 32 8)
                :height (* 4 8)
                :x (- (/ (width screen) 2) (/ (* 32 8) 2))
                :y 150)
          boom (sound "explosion.wav" :play)
          score (assoc (label (str "Your score was " (get-score)) (color :white))
                  :x 200
                  :y (- (height screen) 30))]
      [bg title msg ret score]))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
     (key-pressed? :enter)
     (on-gl (set-screen! space-invaders main-level-screen))))


  :on-resize
  (fn [screen entities]
    (height! screen 600)))







(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!))

  :on-key-down
  (fn [screen entities]
    (cond
     (key-pressed? :r)
     (on-gl (set-screen! space-invaders main-level-screen)))))



(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! space-invaders blank-screen)))))



(defgame space-invaders
  :on-create
  (fn [this]
    (let [music (sound "theme.wav" :loop)]
      (set-screen! this title-screen))))

