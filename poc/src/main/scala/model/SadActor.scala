package model

import akka.actor.Props
import akka.entity.ShardedEntity
import akka.persistence.journal.Tagged
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.serialization.EventSerializer
import play.api.libs.json.Json

class SadActor extends PersistentActor {

  val persistenceId: String = "SadActor|" + self.path.name

  var lastDeliveryId: BigInt = 0

  import SadActor._

  override def receiveCommand: Receive = {

    case SadEcho(aggregateRoot, message, deliveryId) =>
      val sadResponse = message + ":("
      scribe.info(s"[$persistenceId] Received echo! | $message")
      if (deliveryId > lastDeliveryId) {
        persist(
          Tagged(SadEchoed(aggregateRoot, message, deliveryId), Set("SadActor"))
        ) { _ =>
          lastDeliveryId = deliveryId
          sender() ! sadResponse
        }
      } else {
        sender() ! sadResponse
      }

    case other =>
      scribe.error(s"[$persistenceId] Received unexpected message | $other")
  }

  override def receiveRecover: Receive = {
    case _: SadEchoed =>
      scribe.info(s"[$persistenceId] Received :( event")
    case _: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] Recovery completed.")
    case other =>
      scribe.error(s"[$persistenceId] Received unexpected event | $other")
  }
}

object SadActor extends ShardedEntity {

  override def props: Props = Props(new SadActor())

  sealed trait EchoActorCommands extends ddd.Command

  case class SadEcho(aggregateRoot: String, message: String, deliveryId: BigInt)
      extends EchoActorCommands
  case class SadEchoed(aggregateRoot: String,
                       message: String,
                       deliveryId: BigInt)
      extends ddd.Event

  object EventSerializers {
    implicit val SadEchoedF = Json.format[SadEchoed]
    class SadEchoedFS extends EventSerializer[SadEchoed]
  }
}
