(defproject com.polecat/rsocket-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.projectreactor/reactor-core "3.2.5.RELEASE"]
                 [io.projectreactor/reactor-test "3.2.5.RELEASE"]
                 [io.reactivex.rxjava2/rxjava "2.2.6"]
                 [io.rsocket/rsocket-core "0.11.15"]
                 [io.rsocket/rsocket-transport-netty "0.11.15"]
                 [byte-streams "0.2.4"]
                 [io.tsachev.reactor/reactor-core-clojure "0.1.0-SNAPSHOT"]
                 ]
  :repl-options {:init-ns rsocket-clj.core}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :test-paths ["test"])
