(ns ring.middleware.headers
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]
            [ring.util.walk :as ring-walk]))

(defn wrap-friendly-headers
  "Middleware that converts all header:
      keys to keywords in the request, and back to strings in the response.
      values to clojure values in the request, and back to strings in the response.
  Purposed to be used right after the set defined handler, at the beginning of the middleware stack, or first of '->'"
  [handler]
  (fn [request]
    (let [->friendly-headers-req (comp ring-walk/read-val-strings keywordize-keys)
          ->friendly-headers-resp (comp stringify-keys ring-walk/write-val-strings)
          response (handler (update request :headers ->friendly-headers-req))]
      (update response :headers ->friendly-headers-resp))))

(defn wrap-lowercase-headers
  "Middleware that converts all header keys in ring request and response to lowercase strings.
  Assumes that all middleware applied before this is configured with lower-case.
  Prevents outside world from breaking this stack prepared for HTTP/2.
  Prevents any user or middleware set headers from breaking HTTP/2 in the outside world.
  Purposed to be used at the end of the middleware stack, or last of '->'"
  [handler]
  (fn [request]
    (let [response (handler (update request :headers ring-walk/lowerify-keys))]
      (update response :headers ring-walk/lowerify-keys))))
