package model.product_cart.domain

sealed trait ProductCartCommands extends ddd.Command {
  val personId: String
  val productCartId: String

  override def aggregateRoot: String = productCartId
  override def entityId: String = productCartId
  override def shardedId: String = personId
}

object ProductCartCommands {

  case class CreateProductCart(personId: String, productCartId: String, deliveryId: BigInt)
      extends ProductCartCommands

  case class ProductCartUpdateFromPayment(personId: String, productCartId: String, deliveryId: BigInt)
      extends ProductCartCommands

}
