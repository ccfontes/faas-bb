(ns ring.util.time
  "Functions for dealing with time and dates in HTTP requests."
  {:author "James Reeves"
   :contributors "Modified by Carlos da Cunha Fontes to work with Babashka"
   :url "https://github.com/ring-clojure/ring"
   :license {:name "Distributed under the MIT License, the same as Ring."}}
  (:require [clojure.string :as str])
  (:import [java.text ParseException SimpleDateFormat]
           [java.util Locale TimeZone]))

(def ^:no-doc http-date-formats
  {:rfc1123 "EEE, dd MMM yyyy HH:mm:ss zzz"
   :rfc1036 "EEEE, dd-MMM-yy HH:mm:ss zzz"
   :asctime "EEE MMM d HH:mm:ss yyyy"})

(defn- formatter ^SimpleDateFormat [format]
  (doto (SimpleDateFormat. ^String (http-date-formats format) Locale/US)
    (.setTimeZone (TimeZone/getTimeZone "GMT"))))

(defn- attempt-parse [date format]
  (try
    (.parse (formatter format) date)
    (catch ParseException _ nil)))

(defn- trim-quotes [s]
  (str/replace s #"^'|'$" ""))

(defn parse-date
  "Attempt to parse a HTTP date. Returns nil if unsuccessful."
  {:added "1.2"}
  [http-date]
  (->> (keys http-date-formats)
       (map (partial attempt-parse (trim-quotes http-date)))
       (remove nil?)
       (first)))

(defn format-date
  "Format a date as RFC1123 format."
  {:added "1.2"}
  [^java.util.Date date]
  (.format (formatter :rfc1123) date))