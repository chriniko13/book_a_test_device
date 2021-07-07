
## Mob Booking Service

### Description
The purpose of this service is to provide book and return capabilities for mobile devices.


### Prerequisites
First build and run [mob-info-service](../mob-info-service/README.md) it is needed from this service to
fetch additional data.


### Build and run
* Execute `mvn clean install -pl mob-booking-service/`
* Then execute: `java -jar mob-booking-service/target/mob-booking-service-1.0-SNAPSHOT-allinone.jar`
* You should see the following output:
```text
02:55:17.322 [routes-akka.actor.default-dispatcher-3] INFO  c.c.m.b.s.s.MobilePhonesManager - Starting up mobile phones manager...self: Actor[akka://routes/user#0]
02:55:17.326 [routes-akka.actor.default-dispatcher-6] INFO  c.c.m.b.s.s.MobilePhonesStateKeeper - Starting mobile phones state keeper....self: Actor[akka://routes/user/mob-phones-state-keeper#1404386302]
02:55:17.338 [routes-akka.actor.default-blocking-io-dispatcher-15] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/motorola-worker-pool/$a#1421021651]
02:55:17.338 [routes-akka.actor.default-blocking-io-dispatcher-13] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/lg-worker-pool/$a#-1887120260]
02:55:17.339 [routes-akka.actor.default-blocking-io-dispatcher-16] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/nokia-worker-pool/$a#693877409]
02:55:17.342 [routes-akka.actor.default-blocking-io-dispatcher-14] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/huawei-worker-pool/$a#450471084]
02:55:17.342 [routes-akka.actor.default-blocking-io-dispatcher-12] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/apple-worker-pool/$a#-1811966695]
02:55:17.344 [routes-akka.actor.default-blocking-io-dispatcher-11] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/samsung-worker-pool/$a#-647368474]
02:55:17.346 [routes-akka.actor.default-blocking-io-dispatcher-21] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/apple-worker-pool/$b#-87246449]
02:55:17.348 [routes-akka.actor.default-blocking-io-dispatcher-20] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/huawei-worker-pool/$b#-2067691842]
02:55:17.349 [routes-akka.actor.default-blocking-io-dispatcher-19] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/lg-worker-pool/$b#577821558]
02:55:17.349 [routes-akka.actor.default-blocking-io-dispatcher-18] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/nokia-worker-pool/$b#-174054633]
02:55:17.354 [routes-akka.actor.default-blocking-io-dispatcher-22] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/samsung-worker-pool/$b#1715543037]
02:55:17.355 [routes-akka.actor.default-blocking-io-dispatcher-17] INFO  c.c.m.b.s.service.MobilePhonesWorker - Starting mobile phones worker....self: Actor[akka://routes/user/motorola-worker-pool/$b#-568890825]
Server online at http://localhost:8080/
Press RETURN to stop...
-> / LocalActorRefProvider$$anon$2 class akka.actor.LocalActorRefProvider$Guardian status=0 2 children
   ⌊-> system LocalActorRef class akka.actor.LocalActorRefProvider$SystemGuardian status=0 7 children
   |   ⌊-> IO-TCP RepointableActorRef class akka.io.TcpManager status=0 1 children
   |   |   ⌊-> selectors RoutedActorRef class akka.routing.RouterPoolActor status=0 1 children
   |   |       ⌊-> $a LocalActorRef class akka.io.SelectionHandler status=0 no children
   |   ⌊-> Materializers RepointableActorRef class akka.stream.impl.MaterializerGuardian status=0 1 children
   |   |   ⌊-> StreamSupervisor-0 LocalActorRef class akka.stream.impl.StreamSupervisor status=0 1 children
   |   |       ⌊-> flow-0-0-ignoreSink RepointableActorRef class akka.stream.impl.fusing.ActorGraphInterpreter status=2 no children
   |   ⌊-> deadLetterListener RepointableActorRef class akka.event.DeadLetterListener status=0 no children
   |   ⌊-> eventStreamUnsubscriber-1 RepointableActorRef class akka.event.EventStreamUnsubscriber status=0 no children
   |   ⌊-> localReceptionist RepointableActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
   |   ⌊-> log1-Slf4jLogger RepointableActorRef class akka.event.slf4j.Slf4jLogger status=0 no children
   |   ⌊-> pool-master RepointableActorRef class akka.http.impl.engine.client.PoolMasterActor status=0 no children
   ⌊-> user LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 7 children
       ⌊-> apple-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
       |   ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       |   ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> huawei-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
       |   ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       |   ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> lg-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
       |   ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       |   ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> mob-phones-state-keeper LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> motorola-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
       |   ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       |   ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> nokia-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
       |   ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       |   ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
       ⌊-> samsung-worker-pool LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 2 children
           ⌊-> $a LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children
           ⌊-> $b LocalActorRef class akka.actor.typed.internal.adapter.ActorAdapter status=0 no children

```


### TODOs
I am a strong believer of inverted pyramid of testing, I have delivered my solution
with no testing (unit testing, integration testing) which is unacceptable for me, but due
to time limits I have skipped them and do fast manual test from Postman.


### Operations supported as cURLS
* Book a device
```text
curl --location --request PUT 'http://localhost:8080/mobphones/book/7fe1c6e5-7bda-4297-8a70-b9b5d4a87f09' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": "foobar"
}'
```

* View booked devices state
```text
curl --location --request GET 'http://localhost:8080/mobphones' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": "foobar"
}'
```

* Return a device
```text
curl --location --request PUT 'http://localhost:8080/mobphones/return/7fe1c6e5-7bda-4297-8a70-b9b5d4a87f09' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": "foobar"
}'
```