package model.product_cart


import akka.ActorRefMap
import akka.actor.{ActorRef, Props}
import akka.persistence.journal.Tagged
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import model.product_cart.domain.{ProductCartCommands, ProductCartEvents}
import model.payment.PaymentActor
import model.payment.domain.PaymentCommands
import model.person.domain.PersonCommands

class ProductCartActor() extends PersistentActor {
  var state = ProductCartState()

  var childs = new ActorRefMap(
    actorPath => context actorOf (PaymentActor.props, s"$persistenceId-PaymentActor-$actorPath")
  )

  override def receiveCommand: Receive = {

    case (cmd: PaymentCommands, replyTo) =>
      childs(cmd.aggregateRoot) ! ((cmd, replyTo))

    case obligacionResponse @ (akka.Done, replyTo) =>
      context.parent ! obligacionResponse

    case (ProductCartCommands.ProductCartUpdateFromPayment(person, productCart, deliveryId), replyTo) =>
      val event = ProductCartEvents.ProductCartUpdatedFromPayment(person, productCart)
      persistAll(
        Seq(Tagged(event, Set("BuyOrder")), Tagged(event, Set("BuyOrder")))
      ) { _ =>
        state += event
        context.parent ! ((
                            PersonCommands.PersonUpdateFromChilds(
                              person,
                              deliveryId
                            ),
                            replyTo
                          ))
      }

    case (ProductCartCommands.CreateProductCart(person, productCart, deliveryId), replyTo: ActorRef) =>
      val event = ProductCartEvents.ProductCartUpdatedFromPayment(person, productCart)
      persist(
        Tagged(event, Set("ProductCart"))
      ) { _ =>
        state += event
        context.parent ! ((
                            PersonCommands.PersonUpdateFromChilds(
                              person,
                              deliveryId
                            ),
                            replyTo
                          ))

      }

    case responseFromPayment @ (payload, replyTo) =>
      context.parent ! responseFromPayment

    case other => scribe.error(s"[$persistenceId] Unexpected message |  ${other}")

  }

  val persistenceId = self.path.name
  override def receiveRecover: Receive = {

    case evt: ProductCartEvents =>
      state += evt
      scribe.info(s"[$persistenceId] Event received [${evt.toString}]")
    case SnapshotOffer(_, snapshot: ProductCartState) =>
      state = snapshot
    case r: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] recovered with [$r]")
    case unknown =>
      scribe.error(s"[$persistenceId] error received recover with [$unknown]")
  }
}

object ProductCartActor {
  def props: Props = Props(new ProductCartActor)
}
