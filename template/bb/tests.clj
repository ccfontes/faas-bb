(ns tests ^{:author "Carlos da Cunha Fontes"
            :url "https://github.com/ccfontes/faas-bb"
            :license {:name "Distributed under the MIT License"
                      :url "https://github.com/ccfontes/faas-bb/blob/main/LICENSE"}}
  (:require
    [clojure.test :refer [run-tests]]
    [eg :refer [eg]]
    [index]))

(eg index/keywords?
  "true"  => true
  "false" => false
  nil     => true)

(eg index/read-string
  "0A" => "0A"
  "0"  => 0
  "abc" => string?)

(eg index/->kebab-case
  ""        => ""
  "Boo_baR" => "boo-bar")

(eg index/format-context
  {} => {}
  {"Foo_baR" "false"} => {:foo-bar false})

(eg index/->context
  [{} {}] => {:headers {} :env {}}
  [{"Foo_baR" "[]"} {"eggs" "4.3"}] => {:headers {:foo-bar []}
                                        :env {:eggs 4.3}})

(defn arity-2-handler [{:keys [bar]} {:keys [headers env]}]
  [bar (get headers :content-type) (:my-env env)])

(def handler (index/->handler (var arity-2-handler) {"my-env" "env-val"}))

(eg handler
  {:headers {} :body {}} => {:body [nil nil "env-val"] :status 200}
  {:headers {"content-type" "application/json"}, :body {:bar "foo"}} => {:body ["foo" "application/json" "env-val"] :status 200})

(def app (index/->app (var arity-2-handler) {"MY_ENV" "env-val"}))

(def str->stream #(-> % (.getBytes "UTF-8") (java.io.ByteArrayInputStream.)))

(def resp-fixture {:headers {"Content-Type" "application/json; charset=utf-8"}
                   :body "[\"spam\",\"application/json\",\"env-val\"]"
                   :status 200})

(eg app
  {:headers {"content-type" "application/json"}, :body (str->stream "{\"bar\": \"spam\"}")} => resp-fixture)

(let [{:keys [fail error]} (run-tests 'tests)]
  (when (pos? (+ fail error))
    (System/exit 1)))
