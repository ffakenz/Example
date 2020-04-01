package model.product_cart

import ddd._
import model.product_cart.domain._
import model.product_cart.domain.ProductCartEvents

case class ProductCartState(
    )
    extends AbstractState[ProductCartEvents] {

  override def +(event: ProductCartEvents): ProductCartState = event match {
    case evt: ProductCartEvents.CreatedProductCart =>
      this
    case evt: ProductCartEvents.ProductCartUpdatedFromPayment =>
      this

  }

}
