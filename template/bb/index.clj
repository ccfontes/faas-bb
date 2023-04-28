#!/usr/bin/env bb

(require
  '[function.handler :as function]
  '[org.httpkit.server :refer [run-server]])

(run-server function/handler {:port 8082})

@(promise)
