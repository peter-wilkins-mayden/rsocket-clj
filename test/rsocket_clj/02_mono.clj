(ns rsocket-clj.02-mono
  (:require [clojure.test :refer :all]
            [reactor-core.util.function :as f])
  (:import [reactor.test StepVerifier]
           [reactor.core.publisher Flux Mono]
           [java.time Duration]))

;; MONO
(deftest a-test
  (testing "empty Mono"
    (let [mono (Mono/empty)]
      (is (.. (StepVerifier/create mono)
              (verifyComplete)))))

  (testing "no Signal"
    (let [mono (Mono/never)]
      (is (.. (StepVerifier/create mono)
              (expectSubscription)
              (expectNoEvent (Duration/ofSeconds 1))
              (thenCancel)
              (verify)))))

  (testing "fromValue"
    (let [mono (Mono/just "foo")]
      (is (.. (StepVerifier/create mono)
              (expectNext "foo")
              (verifyComplete)))))

  (testing "error"
    (let [mono (Mono/error (IllegalStateException.))]
      (is (.. (StepVerifier/create mono)
              (verifyError IllegalStateException)))))


  (testing "fromValue"
    (let [mono (Mono/just "foo")]
      (is (.. (StepVerifier/create mono)
              (expectNextMatches (f/as-predicate #(= % "foo")))
              (verifyComplete)))))
  )