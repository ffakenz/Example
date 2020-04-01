package model.payment.domain


sealed trait PaymentCommands extends ddd.Command {
  val personId: String
  val productCartId: String
  val paymentId: String

  override def aggregateRoot: String = paymentId
  override def entityId: String = paymentId
  override def shardedId: String = personId
}

object PaymentCommands {
  case class RegisterPayment(
      personId: String,
      productCartId: String,
      paymentId: String,
      deliveryId: BigInt
  ) extends PaymentCommands

}
