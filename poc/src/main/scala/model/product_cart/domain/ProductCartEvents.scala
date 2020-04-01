package model.product_cart.domain

sealed trait ProductCartEvents extends ddd.Event {
  def personId: String
  def productCartId: String

  override def aggregateRoot: String = productCartId
}

object ProductCartEvents {

  case class CreatedProductCart(personId: String, productCartId: String) extends ProductCartEvents

  case class ProductCartUpdatedFromPayment(personId: String, productCartId: String) extends ProductCartEvents

}
