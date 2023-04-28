#!/usr/bin/env bb

(require
  '[function.handler :as function]
  '[org.httpkit.server :refer [run-server]])

(run-server
  function/handler
  {:ip "127.0.0.1" :port 8082})

@(promise)
