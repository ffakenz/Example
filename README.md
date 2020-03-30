# Proof of Bug
This is a **PoC** of the issue at Github for Akka Persistence:
    
https://github.com/akka/akka-persistence-cassandra/issues/706

It reproduces the bug around akka.tag_views:
##### This happens when multiple actors persist tagged events.
```js 
ERROR - Persistence failure when replaying events for persistenceId [model.EchoActor|3]. Last known sequence number [0]
java.util.concurrent.ExecutionException: com.datastax.driver.core.exceptions.ServerError: An unexpected error occurred server side on /0.0.0.0:9042: java.lang.RuntimeException: java.util.concurrent.ExecutionException: org.apache.cassandra.exceptions.ConfigurationException: Column family ID mismatch (found c860d630-7249-11ea-9908-d984957af244; expected c84c8ae0-7249-11ea-9908-d984957af244)
        at com.google.common.util.concurrent.AbstractFuture.getDoneValue(AbstractFuture.java:552)
        at com.google.common.util.concurrent.AbstractFuture.get(AbstractFuture.java:513)
        at akka.persistence.cassandra.package$$anon$1.$anonfun$run$1(package.scala:41)
        at scala.util.Try$.apply(Try.scala:210)
        at akka.persistence.cassandra.package$$anon$1.run(package.scala:41)
        at akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:47)
        at akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(ForkJoinExecutorConfigurator.scala:47)
        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Caused by: com.datastax.driver.core.exceptions.ServerError: An unexpected error occurred server side on /0.0.0.0:9042: java.lang.RuntimeException: java.util.concurrent.ExecutionException: org.apache.cassandra.exceptions.ConfigurationException: Column family ID mismatch (found c860d630-7249-11ea-9908-d984957af244; expected c84c8ae0-7249-11ea-9908-d984957af244)
        at com.datastax.driver.core.Responses$Error.asException(Responses.java:153)
```

----------------------------------------------------

To replicate the bug on your computer run:

```bash
# start up cassandra  
docker-compose -f ./assets/docker-compose.yml down -v
docker-compose -f ./assets/docker-compose.yml up -d

# start up 3 application nodes
sh node1.sh &
sh node2.sh &
sh node3.sh &
sleep 100

# start up the producer main app
sh main.sh
```


##### or just
```ash 
sh runAll.sh
```

----------------------------------------------------
