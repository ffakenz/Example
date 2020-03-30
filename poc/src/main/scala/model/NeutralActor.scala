package model

import akka.actor.Props
import akka.entity.ShardedEntity
import akka.persistence.journal.Tagged
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.serialization.EventSerializer
import play.api.libs.json.{Json, OFormat}

class NeutralActor extends PersistentActor {

  val persistenceId: String = "NeutralActor|" + self.path.name

  var lastDeliveryId: BigInt = 0

  import NeutralActor._

  override def receiveCommand: Receive = {

    case NeutralEcho(aggregateRoot, message, deliveryId) =>
      val neutralResponse = message + ":|"
      scribe.info(s"[$persistenceId] Received echo! | $message")
      if (deliveryId > lastDeliveryId) {
        persist(
          Tagged(
            NeutralEchoed(aggregateRoot, message, deliveryId),
            Set("NeutralActor")
          )
        ) { _ =>
          lastDeliveryId = deliveryId
          sender() ! neutralResponse
        }
      } else {
        sender() ! neutralResponse
      }

    case other =>
      scribe.error(s"[$persistenceId] Received unexpected message | $other")
  }

  override def receiveRecover: Receive = {
    case _: NeutralEchoed =>
      scribe.info(s"[$persistenceId] Received :| event")
    case _: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] Recovery completed.")
    case other =>
      scribe.error(s"[$persistenceId] Received unexpected event | $other")
  }
}

object NeutralActor extends ShardedEntity {

  override def props: Props = Props(new NeutralActor())

  sealed trait EchoActorCommands extends ddd.Command

  case class NeutralEcho(aggregateRoot: String,
                         message: String,
                         deliveryId: BigInt)
      extends EchoActorCommands
  case class NeutralEchoed(aggregateRoot: String,
                           message: String,
                           deliveryId: BigInt)
      extends ddd.Event

  object EventSerializers {
    implicit val NeutralEchoedF: OFormat[NeutralEchoed] =
      Json.format[NeutralEchoed]
    class NeutralEchoedFS extends EventSerializer[NeutralEchoed]
  }
}
