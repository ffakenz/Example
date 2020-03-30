package model

import akka.actor.Props
import akka.entity.ShardedEntity
import akka.persistence.journal.Tagged
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.serialization.EventSerializer
import model.HappyActor.{HappyEcho, HappyEchoed}
import play.api.libs.json.Json

class HappyActor extends PersistentActor {

  val persistenceId: String = "HappyActor|" + self.path.name

  var lastDeliveryId: BigInt = 0

  override def receiveCommand: Receive = {
    case HappyEcho(aggregateRoot, message, deliveryId) =>
      val happyResponse = message + ":)"
      scribe.info(s"[$persistenceId] Received a happy echo! | $message")
      if (deliveryId > lastDeliveryId) {
        persist(
          Tagged(
            HappyEchoed(aggregateRoot, message, deliveryId),
            Set("HappyActor")
          )
        ) { _ =>
          scribe.info(
            s"[$persistenceId] Persisted message to the journal! | $message"
          )
          lastDeliveryId = deliveryId
          sender() ! happyResponse
        }
      } else {
        sender() ! happyResponse
      }

    case other =>
      scribe.error(s"[$persistenceId] Received unexpected message | $other")
  }

  override def receiveRecover: Receive = {
    case _: HappyEchoed =>
      scribe.info(s"[$persistenceId] Received :) event")
    case _: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] Recovery completed.")
    case other =>
      scribe.error(s"[$persistenceId] Received unexpected event | $other")
  }
}

object HappyActor extends ShardedEntity {

  override def props: Props = Props(new HappyActor())

  sealed trait EchoActorCommands extends ddd.Command

  case class HappyEcho(aggregateRoot: String,
                       message: String,
                       deliveryId: BigInt)
      extends EchoActorCommands
  case class HappyEchoed(aggregateRoot: String,
                         message: String,
                         deliveryId: BigInt)
      extends ddd.Event

  object EventSerializers {
    implicit val HappyEchoedF = Json.format[HappyEchoed]
    class HappyEchoedFS extends EventSerializer[HappyEchoed]
  }
}
