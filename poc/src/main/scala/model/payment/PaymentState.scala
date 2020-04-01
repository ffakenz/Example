package model.payment

import java.time.LocalDateTime

import ddd._
import model.payment.domain.{PaymentsEvents}

case class PaymentState(
    )
    extends AbstractState[PaymentsEvents] {
  override def +(event: PaymentsEvents): PaymentState = event match {
    case evt: PaymentsEvents.RegisteredPayment =>
      this
  }

}
