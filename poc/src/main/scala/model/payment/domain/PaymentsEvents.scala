package model.payment.domain

sealed trait PaymentsEvents extends ddd.Event {
  def paymentId: String

  override def aggregateRoot: String = paymentId
}

object PaymentsEvents {
  case class RegisteredPayment(
      paymentId: String
  ) extends PaymentsEvents
}
