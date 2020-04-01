# Proof of Bug
This is a **PoC** of the issue at Github for Akka Persistence:
    
https://github.com/akka/akka-persistence-cassandra/issues/706

It reproduces the bug around akka.tag_views:
##### This happens when multiple actors persist tagged events.
```js 
ERROR - Cassandra Journal has experienced an unexpected error and requires an ActorSystem restart.
java.lang.IllegalStateException: Expected events to be ordered by seqNr. PersonActor-1 Events: Vector(
    (PersonActor-1,1,bc5cf490-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,1,bc690280-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,2,bc690281-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,3,bc77f6a0-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,4,bc77f6a1-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,5,bc7c1550-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,6,bc7c1551-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,7,bc7dea10-7447-11ea-9c03-8f2919737ff1),
    (PersonActor-1,8,bc7dea11-7447-11ea-9c03-8f2919737ff1)
)
    at akka.persistence.cassandra.journal.TagWriter.$anonfun$createTagWriteSummary$1(TagWriter.scala:359)
    at scala.collection.IterableOnceOps.foldLeft(IterableOnce.scala:636)
    at scala.collection.IterableOnceOps.foldLeft$(IterableOnce.scala:632)
    at scala.collection.AbstractIterable.foldLeft(Iterable.scala:921)
    at akka.persistence.cassandra.journal.TagWriter.createTagWriteSummary(TagWriter.scala:352)
    at akka.persistence.cassandra.journal.TagWriter.akka$persistence$cassandra$journal$TagWriter$$write(TagWriter.scala:337)
    at akka.persistence.cassandra.journal.TagWriter$$anonfun$akka$persistence$cassandra$journal$TagWriter$$idle$1.applyOrElse(TagWriter.scala:148)
    at akka.actor.Actor.aroundReceive(Actor.scala:534)
    at akka.actor.Actor.aroundReceive$(Actor.scala:532)
    at akka.persistence.cassandra.journal.TagWriter.akka$actor$Timers$$super$aroundReceive(TagWriter.scala:98)
    at akka.actor.Timers.aroundReceive(Timers.scala:51)
    at akka.actor.Timers.aroundReceive$(Timers.scala:40)
    at akka.persistence.cassandra.journal.TagWriter.aroundReceive(TagWriter.scala:98)
    at akka.actor.ActorCell.receiveMessage(ActorCell.scala:573)
    at akka.actor.ActorCell.invoke(ActorCell.scala:543)
    at akka.dispatch.Mailbox.processMailbox(Mailbox.scala:269)
    at akka.dispatch.Mailbox.run(Mailbox.scala:230)
    at akka.dispatch.Mailbox.exec(Mailbox.scala:242)
    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
    at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
    at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
    at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
```

----------------------------------------------------

To replicate the bug on your computer:

- First run start up the cassandra container:
```
docker-compose -f ./assets/docker-compose.yml up -d
```
- Then initialize the cassandra db
```
docker exec -i cassandra cqlsh < setup_akka_tables.cql
```
- Finally start up the producer main app
```
sh main.sh
```
- In order to take release resources, please do not forget to run 
```ash 
sh shut_down.sh
```

Please note the error does not have a 100% guarantee to be reproduced.
So please repeat the steps above if needed until it happens.

