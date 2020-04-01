package model.payment

import akka.actor.{ActorRef, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import akka.persistence.journal.Tagged
import model.payment.domain.{PaymentCommands, PaymentsEvents}
import model.payment.domain.PaymentCommands.RegisterPayment
import model.product_cart.domain.ProductCartCommands

class PaymentActor() extends PersistentActor {
  var state = PaymentState()

  var lastDeliveryId: BigInt = 0

  override def receiveCommand: Receive = {
    case (cmd: PaymentCommands, replyTo: ActorRef) =>
      cmd match {
        case RegisterPayment(personId, productCartId, paymentId, deliveryId) =>
          if (!(deliveryId > lastDeliveryId)) {
            replyTo ! akka.Done
          } else {
            val event = PaymentsEvents.RegisteredPayment(paymentId)
            persist(Tagged(event, Set("Payments"))) { _ =>
              state += event
              lastDeliveryId = deliveryId
              context.parent ! ((ProductCartCommands.ProductCartUpdateFromPayment(personId, productCartId, deliveryId),
                                 replyTo))
            }
          }
        case other =>
          scribe.info(s"[$persistenceId] Unexpected message |  ${other}")
      }

    case other => scribe.error(s"[$persistenceId] Unexpected message |  ${other}")

  }

  val persistenceId = self.path.name
  override def receiveRecover: Receive = {

    case evt: PaymentsEvents =>
      state += evt
      scribe.info(s"[$persistenceId] Event received [${evt.toString}]")
    case SnapshotOffer(_, snapshot: PaymentState) =>
      state = snapshot
    case r: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] recovered with [$r]")
    case unknown =>
      scribe.error(s"[$persistenceId] error received recover with [$unknown]")
  }
}

object PaymentActor {
  def props: Props = Props(new PaymentActor)
}
