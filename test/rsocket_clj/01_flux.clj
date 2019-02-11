(ns rsocket-clj.01-flux
  (:require [clojure.test :refer :all]
            [reactor-core.publisher :as rc])
  (:import [reactor.test StepVerifier]
           [reactor.core.publisher Flux Mono]
           [java.time Duration]))

;;FLUX
(deftest a-test
  (testing "Empty flux"
    (let [flux (Flux/empty)]
      (is (.. (StepVerifier/create flux)
              (verifyComplete)))))

  (comment
    (testing "from Values"
      (let [flux nil]
        (is (.. (StepVerifier/create flux)
                (expectNext "foo" "bar")
                (verifyComplete))))))

  (testing "from list"
    (let [flux (Flux/fromIterable ["foo" "bar"])]
      (is (.. (StepVerifier/create flux)
              (expectNext "foo" "bar")
              (verifyComplete)))))

  (testing "from error"
    (let [flux (Flux/error (IllegalStateException.))]
      (is (.. (StepVerifier/create flux)
              (verifyError)))))

  (testing "from interval"
    (let [flux (.. (Flux/interval (Duration/ofMillis 100))
                   (take 10))]
      (is (.. (StepVerifier/create flux)
              (expectNextCount 10)
              (verifyComplete)))))

  )