(ns tests
  {:author "Carlos da Cunha Fontes"
   :url "https://github.com/ccfontes/faas-bb"
   :license {:name "Distributed under the MIT License"
             :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [ring.middleware.text :as middleware-text]
    [ring.util.walk :as ring-walk]
    [ring.util.string :as ring-string]
    [index]))

(eg middleware-text/text-request?
  {:headers {}} => false
  {:headers {"content-type" "text/plain"}} => true
  {:headers {"content-type" "application/json"}} => false)

(eg ring-string/read-string
  "0A"   => "0A"
  "0"    => 0
  "-1.1" => -1.1
  "true" => true
  ""     => nil
  "abc"  => "abc"
  ":def" => ":def")

(eg ring-string/write-string
  -4    => "-4"
  5.3   => "5.3"
  false => "false"
  nil   => ""
  "asd" => "asd"
  :qwer => "qwer")

(eg index/keywords?
  true  => true
  false => false
  nil   => true)

(eg ring-string/->kebab-case
  ""        => ""
  "Boo_baR" => "boo-bar")

(eg ring-walk/format-context
  {} => {}
  {"Foo_baR" "false"} => {:foo-bar false})

(eg ring-walk/read-val-strings
  {} => {}
  {"Foo_baR" "false"} => {"Foo_baR" false})

(eg ring-walk/write-val-strings
  {} => {}
  {"Foo_baR" 1} => {"Foo_baR" "1"})

(eg ring-walk/lowerify-keys
  {} => {}
  {"Foo-baR" "abc"} => {"foo-bar" "abc"})

(def def-has-no-arglists (fn [_]))

(eg index/fn-arg-cnt
  #'clojure-version => 0
  #'identity => 1
  #'compare => 2
  #'def-has-no-arglists => nil)

(eg index/wrap-arg
  #'clojure-version => #(string? (% 10))
  #'identity => #(= 10 (% 10)))

(defn user-handler [{:keys [body headers context]}]
  [(:bar body) (or (get headers "content-type") (:content-type headers)) (:my-env context)])

(def handler (index/->handler user-handler {:my-env "env-val"}))

(eg handler
  {:headers {} :body {}} => {:headers {} :body [nil nil "env-val"] :status 200}
  {:headers {"content-type" "application/json"}, :body {:bar "foo"}} => {:headers {} :body ["foo" "application/json" "env-val"] :status 200})

(def app (index/->app user-handler {:my-env "env-val"}))

(def str->stream #(-> % (.getBytes "UTF-8") (java.io.ByteArrayInputStream.)))

(def resp-fixture {:headers {"content-type" "application/json; charset=utf-8"}
                   :body "[\"spam\",\"application/json; charset=utf-8\",\"env-val\"]"
                   :status 200})

(eg app
  {:headers {"content-type" "application/json; charset=utf-8"}, :body (str->stream "{\"bar\": \"spam\"}")} => resp-fixture)

(let [{:keys [fail error]} (run-tests 'tests)]
  (when (pos? (+ fail error))
    (System/exit 1)))
