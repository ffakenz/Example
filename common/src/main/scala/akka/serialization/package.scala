package akka
import play.api.libs.json.{Format, Json}
import scala.reflect.ClassTag

package object serialization {

  def encode[A](a: A)(implicit format: Format[A]): String =
    Json.prettyPrint(format.writes(a))

  def decode[A: ClassTag](a: String)(implicit format: Format[A]): Either[String, A] = {
    def ctag = implicitly[reflect.ClassTag[A]]
    def AClass: Class[A] = ctag.runtimeClass.asInstanceOf[Class[A]]
    AClass.getName == "java.lang.String" match {
      case true => Right(a.asInstanceOf[A])
      case false =>
        Json.parse(a).asOpt[A] match {
          case Some(a) => Right(a)
          case None => Left(s"Failed to decode ${AClass.getName} $a)}")
        }
    }
  }
}
