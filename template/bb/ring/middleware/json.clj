(ns ring.middleware.json
  "Ring middleware for parsing JSON requests and generating JSON responses."
  ^{:author "James Reeves"
    :contributors "Modified by Carlos da Cunha Fontes to work with Babashka"
    :url "https://github.com/ring-clojure/ring-json"
    :license {:name "Distributed under the MIT License, the same as Ring."}}
  (:require [cheshire.core :as json])
  (:import [java.io InputStream]))

(def ^{:doc "HTTP token: 1*<any CHAR except CTLs or tspecials>. See RFC2068"}
  re-token
  #"[!#$%&'*\-+.0-9A-Z\^_`a-z\|~]+")

(def ^{:doc "HTTP quoted-string: <\"> *<any TEXT except \"> <\">. See RFC2068."}
  re-quoted
  #"\"((?:\\\"|[^\"])*)\"")

(def ^{:doc "HTTP value: token | quoted-string. See RFC2109"}
  re-value
  (str "(" re-token ")|" re-quoted))

(def ^{:doc "Pattern for pulling the charset out of the content-type header"}
  re-charset
  (re-pattern (str ";(?:.*\\s)?(?i:charset)=(?:" re-value ")\\s*(?:;|$)")))

(defn find-content-type-charset
  "Return the charset of a given a content-type string."
  [s]
  (when-let [m (re-find re-charset s)]
    (or (m 1) (m 2))))

(defn character-encoding
  "Return the character encoding for the request, or nil if it is not set."
  [request]
  (some-> (get-in request [:headers "content-type"])
          find-content-type-charset))

(defn header
  "Returns an updated Ring response with the specified header added."
  [resp name value]
  (assoc-in resp [:headers name] (str value)))

(defn content-type
  "Returns an updated Ring response with the a Content-Type header corresponding
  to the given content-type."
  [resp content-type]
  (header resp "Content-Type" content-type))

(defn- json-request? [request]
  (when-let [type (get-in request [:headers "content-type"])]
    (seq (re-find #"^application/(.+\+)?json" type))))

(defn- read-json [request & [{:keys [keywords? key-fn]}]]
  (when (json-request? request)
    (when-let [^InputStream body (:body request)]
      (let [^String encoding (or (character-encoding request)
                                 "UTF-8")
            body-reader (java.io.InputStreamReader. body encoding)]
        (try
          [true (json/parse-stream body-reader (or key-fn keywords?))]
          (catch Exception _
            (println "Error parsing json stream")
            [false nil]))))))

(def ^{:doc "The default response to return when a JSON request is malformed."}
  default-malformed-response
  {:status  400
   :headers {"Content-Type" "text/plain"}
   :body    "Malformed JSON in request body."})

(defn json-body-request
  "Parse a JSON request body and assoc it back into the :body key. Returns nil
  if the JSON is malformed. See: wrap-json-body."
  [request options]
  (if-let [[valid? json] (read-json request options)]
    (when valid? (assoc request :body json))
    request))

(defn wrap-json-body
  "Middleware that parses the body of JSON request maps, and replaces the :body
  key with the parsed data structure. Requests without a JSON content type are
  unaffected.

  Accepts the following options:

  :key-fn             - function that will be applied to each key
  :keywords?          - true if the keys of maps should be turned into keywords
  :bigdecimals?       - true if BigDecimals should be used instead of Doubles
  :malformed-response - a response map to return when the JSON is malformed"
  {:arglists '([handler] [handler options])}
  [handler & [{:keys [malformed-response]
               :or {malformed-response default-malformed-response}
               :as options}]]
  (fn
    ([request]
     (if-let [request (json-body-request request options)]
       (handler request)
       malformed-response))
    ([request respond raise]
     (if-let [request (json-body-request request options)]
       (handler request respond raise)
       (respond malformed-response)))))

(defn- assoc-json-params [request json]
  (if (map? json)
    (-> request
        (assoc :json-params json)
        (update-in [:params] merge json))
    request))

(defn json-params-request
  "Parse the body of JSON requests into a map of parameters, which are added
  to the request map on the :json-params and :params keys. Returns nil if the
  JSON is malformed. See: wrap-json-params."
  [request options]
  (if-let [[valid? json] (read-json request options)]
    (when valid? (assoc-json-params request json))
    request))

(defn wrap-json-params
  "Middleware that parses the body of JSON requests into a map of parameters,
  which are added to the request map on the :json-params and :params keys.

  Accepts the following options:

  :key-fn             - function that will be applied to each key
  :bigdecimals?       - true if BigDecimals should be used instead of Doubles
  :malformed-response - a response map to return when the JSON is malformed

  Use the standard Ring middleware, ring.middleware.keyword-params, to
  convert the parameters into keywords."
  {:arglists '([handler] [handler options])}
  [handler & [{:keys [malformed-response]
               :or {malformed-response default-malformed-response}
               :as options}]]
  (fn
    ([request]
     (if-let [request (json-params-request request options)]
       (handler request)
       malformed-response))
    ([request respond raise]
     (if-let [request (json-params-request request options)]
       (handler request respond raise)
       (respond malformed-response)))))

(defn json-response
  "Converts responses with a map or a vector for a body into a JSON response.
  See: wrap-json-response."
  [response options]
  (if (coll? (:body response))
    (let [json-resp (update-in response [:body] json/generate-string options)]
      (if (contains? (:headers response) "Content-Type")
        json-resp
        (content-type json-resp "application/json; charset=utf-8")))
    response))

(defn wrap-json-response
  "Middleware that converts responses with a map or a vector for a body into a
  JSON response.

  Accepts the following options:

  :key-fn            - function that will be applied to each key
  :pretty            - true if the JSON should be pretty-printed
  :escape-non-ascii  - true if non-ASCII characters should be escaped with \\u
  :stream?           - true to create JSON body as stream rather than string"
  {:arglists '([handler] [handler options])}
  [handler & [{:as options}]]
  (fn
    ([request]
     (json-response (handler request) options))
    ([request respond raise]
     (handler request (fn [response] (respond (json-response response options))) raise))))
