import akka.actor.{ActorRef, ActorSystem}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.serialization.EventSerializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import model.HappyActor.HappyEcho
import model.NeutralActor.NeutralEcho
import model.SadActor.SadEcho
import model.{HappyActor, NeutralActor, SadActor}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

object Node extends App {

  lazy val config = Seq(
    ConfigFactory parseString EventSerializer.eventAdapterConf,
    ConfigFactory parseString EventSerializer.serializationConf,
    ConfigFactory.load()
  ).reduce(_ withFallback _)

  implicit val system: ActorSystem =
    ActorSystem("ExampleSystem", config)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = 10 seconds

  scribe.info("Starting up Akka Cluster")
  AkkaManagement(system).start()
  ClusterBootstrap(system).start()

  Await.result(system.whenTerminated, Duration.Inf)

}
