package serialization

import model.product_cart.domain.ProductCartEvents.{CreatedProductCart, ProductCartUpdatedFromPayment}
import model.payment.domain.PaymentsEvents.RegisteredPayment
import model.person.domain.PersonEvents.{PersonUpdatedStatus, PersonUpdatedFromChilds, PersonUpdatedStatusV2}
import play.api.libs.json.Json

object JsonFormats {

  object EventSerializer {

    // Payment
    implicit val RegisteredPaymentF = Json.format[RegisteredPayment]
    class RegisteredPaymentFS extends EventSerializer[RegisteredPayment]

    // ProductCart
    implicit val CreatedProductCartF = Json.format[CreatedProductCart]
    class CreatedProductCartFS extends EventSerializer[CreatedProductCart]
    implicit val ProductCartUpdatedFromPaymentF = Json.format[ProductCartUpdatedFromPayment]
    class ProductCartUpdatedFromPaymentFS extends EventSerializer[ProductCartUpdatedFromPayment]

    // Person
    implicit val PersonUpdatedStatusF = Json.format[PersonUpdatedStatus]
    class PersonUpdatedStatusFS extends EventSerializer[PersonUpdatedStatus]
    implicit val PersonUpdatedFromChildsF = Json.format[PersonUpdatedFromChilds]
    class PersonUpdatedFromChildsFS extends EventSerializer[PersonUpdatedFromChilds]
    implicit val PersonUpdatedStatusV2F = Json.format[PersonUpdatedStatusV2]
    class PersonUpdatedStatusV2FS extends EventSerializer[PersonUpdatedStatusV2]

  }

}
