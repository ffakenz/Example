import akka.entity.ShardedEntity.NoRequirements
import akka.actor.{ActorRef, ActorSystem}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import model.person.PersonActor
import serialization.EventSerializer

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

object Main extends App {

  lazy val config = Seq(
    ConfigFactory parseString EventSerializer.eventAdapterConf,
    ConfigFactory parseString EventSerializer.serializationConf,
    ConfigFactory.load()
  ).reduce(_ withFallback _)

  implicit val system: ActorSystem = ActorSystem("ClusterExample", config)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = 10 seconds

  scribe.info("Starting up Akka Cluster")
  AkkaManagement(system).start()
  ClusterBootstrap(system).start()

  val person: ActorRef = PersonActor.start(NoRequirements())



  person ! model.person.domain.PersonCommands.PersonUpdateStatus(
    aggregateRoot = "1",
    deliveryId = 1,
    status = "buying groceries at the supermarket!"
  )

  person ! model.product_cart.domain.ProductCartCommands.CreateProductCart(
    personId = "1",
    productCartId = "1",
    deliveryId = 2
  )

  person ! model.product_cart.domain.ProductCartCommands.CreateProductCart(
    personId = "1",
    productCartId = "1",
    deliveryId = 3
  )

  person ! model.payment.domain.PaymentCommands.RegisterPayment(
    personId = "1",
    productCartId = "1",
    paymentId = "1",
    deliveryId = 4
  )

  scribe.info("Sent all messages? and what?!")
  Await.result(system.whenTerminated, Duration.Inf)

}
