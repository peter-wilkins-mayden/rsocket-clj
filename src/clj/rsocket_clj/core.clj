(ns rsocket-clj.core
  (:refer-clojure :exclude [map flatmap vector-of concat empty filter map range reduce take])
  (:require [byte-streams :refer :all]
            [reactor-core.publisher :as p]
            [reactor-core.util.function :refer :all])
  (:import [io.rsocket RSocketFactory AbstractRSocket SocketAcceptor]
           [reactor.core.publisher Mono Flux]
           [io.rsocket.transport.netty.server TcpServerTransport]
           [io.rsocket.util DefaultPayload]
           [io.rsocket.transport.netty.client TcpClientTransport]
           [java.time Duration]
           [com.polecat.rsocket_clj DataPublisher GameController]))

(defn server-rsocket [^DataPublisher dp ^GameController gc]
  (.. (RSocketFactory/receive)
      (acceptor (reify SocketAcceptor
                  (accept [this setup sendingSocket]
                    (Mono/just (proxy [AbstractRSocket] []
                                 (requestResponse [data]
                                   (try
                                     (Mono/just data)
                                     (catch Exception e
                                       (Mono/error e))))
                                 (fireAndForget [data]
                                   (try
                                     (.publish dp data)
                                     (Mono/empty)
                                     (catch Exception e
                                       (Mono/error e))))
                                 (requestStream [data]
                                   (Flux/from dp))
                                 (requestChannel [them]
                                   (do
                                     (.subscribe (Flux/from them) (as-consumer #(.processPayload gc %)))
                                     (Flux/from gc))))))))
      (transport (TcpServerTransport/create "localhost", 8979))
      (start)
      (subscribe)))

(defn client-rsocket []
  (.. (RSocketFactory/connect)
      (transport (TcpClientTransport/create "localhost", 8979))
      (start)
      (block)))

(defn marco-polo [socket string]
  (.. socket
      (requestResponse (DefaultPayload/create string))
      (map (as-function #(DefaultPayload/create (to-byte-buffer (str %)))))
      (block)))

(defn fire-and-forget [socket]
  (->> (p/interval (Duration/ofMillis 100))
       (p/take 10)
       (p/map (as-function #(DefaultPayload/create (to-byte-buffer (str %)))))
       (p/flat-map (as-function #(.fireAndForget socket %)))
       (.blockLast)))

(defn request-stream [socket]
  (.. socket
      (requestStream (DefaultPayload/create "stream name"))
      (map (as-function #(byte-streams/convert (.getData %) String)))))

(defn play-game [socket player2]
  (.. socket
      (requestChannel (Flux/from player2))
      (doOnNext (as-consumer #(.processPayload player2 %)))
      (blockLast)))

(comment

  (def dp (DataPublisher.))
  (def gc (GameController. "pete"))
  (def server-rsocket (server-rsocket dp gc))
  (def client (client-rsocket))

  (p/subscribe (marco-polo client "marco!") #(println %))
  (byte-streams/convert (.getData thing) String)
  (.. (request-stream client)
      (subscribe (as-consumer #(println %))
                 (as-consumer #(println %))))
  (fire-and-forget client)

  (def player2 (GameController. "player2"))
  (play-game client player2)
  )