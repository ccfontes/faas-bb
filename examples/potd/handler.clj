(ns handler)

(def proverbs
  ["Actions speak louder than words"
   "Beggars cannot be choosers"
   "Bite the bullet"
   "Better late than never"
   "All that glitters is not gold"
   "Cry over spilt milk"
   "Better safe than sorry"
   "A bad workman blames his tools"])

(defn choose-rand-proverb []
  (nth proverbs (rand-int (count proverbs))))

(defn handler []
  (choose-rand-proverb))
