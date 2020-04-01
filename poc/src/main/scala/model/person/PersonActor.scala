package model.person

import java.time.LocalDateTime

import akka.ActorRefMap
import akka.actor.{ActorRef, Props}
import akka.entity.ShardedEntity
import akka.entity.ShardedEntity.NoRequirements
import akka.persistence.journal.Tagged
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import model.payment.domain.PaymentCommands
import model.person.domain.PersonEvents.PersonUpdatedStatusV2
import model.person.domain.{PersonCommands, PersonEvents}
import model.product_cart.ProductCartActor
import model.product_cart.domain.ProductCartCommands

class PersonActor() extends PersistentActor {
  var state = PersonState()

  var childs = new ActorRefMap(
    actorPath => context actorOf (ProductCartActor.props, s"$persistenceId-ProductCartActor-$actorPath")
  )

  override def receiveCommand: Receive = {
    case (PersonCommands.PersonUpdateFromChilds(aggregateRoot, deliveryId), replyTo: ActorRef) =>
      val event = PersonEvents.PersonUpdatedFromChilds(aggregateRoot)
      persistAll(
        Seq(Tagged(event, Set("Person")), Tagged(event, Set("Person")))
      ) { _ =>
        state += event
      }
    case PersonCommands.PersonUpdateStatus(aggregateRoot, deliveryId, status) =>
      val event = PersonEvents.PersonUpdatedStatus(aggregateRoot, status)
      persist(Tagged(event, Set("Person"))) { _ =>
        state += event
      }

    case childMessage: ProductCartCommands =>
      scribe.info(s"Person child message: $childMessage")
      childs(childMessage.productCartId) ! ((childMessage, sender()))

    case childMessage: PaymentCommands =>
      scribe.info(s"Person child message: $childMessage")
      childs(childMessage.productCartId) ! ((childMessage, sender()))

    case childResponse @ (payload, replyTo: ActorRef) => ()

    case other => scribe.error(s"[$persistenceId] Unexpected message |  ${other}")

  }

  val persistenceId = s"PersonActor-${self.path.name}"
  override def receiveRecover: Receive = {

    case evt: PersonEvents =>
      state += evt
      scribe.info(s"[$persistenceId] Event received [when${evt.toString}]")
    case SnapshotOffer(_, snapshot: PersonState) =>
      state = snapshot
    case r: RecoveryCompleted =>
      scribe.info(s"[$persistenceId] recovered with [$r]")
    case unknown =>
      scribe.error(s"[$persistenceId] error received recover with [$unknown]")
  }
}

object PersonActor extends ShardedEntity[NoRequirements] {
  def props(noRequirements: NoRequirements = NoRequirements()): Props = Props(new PersonActor)
}
