(ns tests
  (:require
    [index]
    [clojure.test :refer [deftest is run-tests]]
    [clojure.spec.alpha :as spec]
    [clojure.spec.test.alpha :as spec-test]
    [eg :refer [eg]]
    [plumula.mimolette.alpha :refer [defspec-test]]))

;(deftest foo-test
;  (is (= )))
;(spec/def ::keywords? (nilable #{true false}))
;(spec/def ::response (map-of keyword? string?))
; fn-arg-cnt:  (-> #'function/handler (meta) :arglists (first) (count))

(spec/def :str/headers (spec/map-of string? string?))
(eg :str/headers {"foo" "bar"})
(spec/def :edn/headers (spec/map-of keyword? any?))
(eg :edn/headers {:foo 3})

(spec/def ::body (spec/map-of (spec/or :kw keyword? :str string?) any?))
(eg ::body {"foo" "bar"})
(eg ::body {:foo "bar"})

(spec/def :str/env (spec/map-of string? string?))
(eg :str/env {"foo" "bar"})
(spec/def :edn/env (spec/map-of keyword? any?))
(eg :edn/env {:foo "bar"})

(spec/def ::request (spec/keys :req-un [:str/headers ::body]))
(eg ::request {:headers {"foo" "bar"}
               :body {"foo" "bar"}})

(spec/def ::response (spec/keys :req-un [:kw/headers ::body]))
(eg ::response {:headers {:foo "bar"}
                :body {:foo "bar"}})

(spec/def ::handler #{identity #(conj [%1] %2)})

(spec/fdef index/read-string
  :args (spec/cat :str string?)
  :ret any?)

(spec/fdef index/keywords?
  :args (spec/cat :str (spec/nilable #{"true" "false"}))
  :ret boolean?)

(eg index/read-string
  "0A" => "0A"
  "0"  => 0)

(spec/fdef index/->kebab-case
  :args (spec/cat :str string?)
  :ret string?)

(spec/fdef index/format-context
  :args (spec/cat :context-map (spec/map-of string? string?))
  :ret (spec/map-of keyword? any?))

(spec/fdef index/->context
  :args (spec/cat :request :str/headers
                  :env :edn/env)
  :ret (spec/keys :req-un [:edn/headers :edn/env]))

(spec/fdef index/->handler
  :args (spec/cat :fn ::handler
                  :env :str/env)
  :ret fn?)

(spec/fdef index/->app
  :args (spec/cat :fn ::handler
                  :env :str/env)
  :ret fn?)

; ring tests

(defspec-test spec-check-index (spec-test/enumerate-namespace 'index))

(clojure.test/run-tests 'tests)
