package akka.serialization

import java.io.NotSerializableException
import java.nio.charset.Charset

import scala.reflect.ClassTag

import akka.serialization.SerializerWithStringManifest
import org.slf4j.LoggerFactory
import play.api.libs.json.Format

/* Simplest possible serializer, uses a string representation of the class.
 *
 * Usually a serializer like this would use a library like:
 * protobuf, kryo, avro, cap'n proto, flatbuffers, SBE or some other dedicated serializer backend
 * to perform the actual to/from bytes marshalling.
 */
abstract class EventSerializer[A <: AnyRef: ClassTag](
    implicit serializer: Format[A]
) extends SerializerWithStringManifest { self =>

  override def identifier: Int = EventSerializer.identify(self.getClass.getName)

  private val Utf8 = Charset.forName("UTF-8")

  // extract manifest to be stored together with serialized object
  override def manifest(o: AnyRef): String = o.getClass.getName

  // serialize the object
  override def toBinary(obj: AnyRef): Array[Byte] = {
    obj match {
      case p: A =>
        val str = encode(p)
        str.getBytes(Utf8)
      case _ =>
        throw new IllegalArgumentException(
          s"Unable to serialize to bytes, clazz was: ${obj.getClass}!"
        )
    }
  }

  // deserialize the object, using the manifest to indicate which logic to apply
  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    val asString = new String(bytes, Utf8)
    decode(asString) match {
      case Right(a) =>
        scribe.info(s"Event Deserialized with success. ${a}")
        a
      case Left("Failed to decode") =>
        val error = s"Unable to deserialize from bytes, manifest was: $manifest! Bytes length: " + bytes.length
        scribe.error(error)
        throw new NotSerializableException(error)
      case Left(customMessage) =>
        scribe.error(customMessage)
        throw new NotSerializableException(customMessage)
    }
  }
}

object EventSerializer {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private lazy val EventSerializersNetworkIdentities = utils.Inference
    .getSubtypesOf[EventSerializer[_]]
    .map { _.getName }
    .toSeq
    .map { name: String =>
      name -> (name.hashCode + 100)
    } // this is due to the first 20 ids are reserved by akka
    .toMap

  def identify(s: String): Int =
    EventSerializersNetworkIdentities(s)

  import utils.Inference.{getSimpleName => name}
  def getClassName(event: Class[_]) = event.toString.replace("class ", "")

  def cleanName(name: String): String =
    name.split('$').last.replace("FS", "")

  def getSerializersOf[Serializable: ClassTag, Serializer: ClassTag] = {
    val events = utils.Inference
      .getSubtypesOf[Serializable]
      .toSeq
      .filter(_.toString contains "class")
    val eventsSerializers = utils.Inference
      .getSubtypesOf[Serializer]

    val inference = events.map { event =>
      val eventName = name(getClassName(event))
      val eventSerializer = eventsSerializers.map(_.getName) find { eventSerializerName =>
        logger.debug(
          s"[VERIFYING] {} for event {}",
          eventSerializerName,
          eventName
        )
        val serializerNameCleaned = cleanName(eventSerializerName)
        val eventNameCleaned = cleanName(eventName)
        val equals = cleanName(eventSerializerName) == cleanName(eventName)
        logger.debug(
          "{} == {} = {}",
          serializerNameCleaned,
          eventNameCleaned,
          equals
        )
        equals
      }
      (event, eventSerializer)
    }

    val eventWithSerializer = inference collect {
        case (eventName, Some(eventSerializer)) => (eventName, eventSerializer)
      }
    val eventWithoutSerializer = inference collect {
        case (eventName, None) => eventName
      }

    eventWithoutSerializer foreach (
        eventName =>
          throw new Exception(
            s"You forgot to make the ${eventName}FS serializer."
          )
      )
    // Now we know for sure that we have ALL Events correctly mapped out to EventSerializers.
    // Automation time!

    eventWithSerializer

  }

  def eventWithSerializer = getSerializersOf[ddd.Event, EventSerializer[_]]
  //def commandWithSerializer = getSerializersOf[ddd.Command, Trait]

  def serializationConf =
    s"""
    akka {
      actor {
        serializers {

          ${eventWithSerializer.toSeq.map {
      case (event, eventSerializer) =>
        s""" 
                 ${name(eventSerializer)} = "${eventSerializer.toString}" 
               """
    } map ("         " + _) mkString "\n"}

        }
        serialization-bindings {
            ${eventWithSerializer.toSeq.map {
      case (event, eventSerializer) =>
        s""" "${getClassName(event)}" = ${name(eventSerializer)} """
    } map ("         " + _) mkString "\n"}
        }
      }
    }
  """

  def eventAdapterConf =
    s"""
    event-adapters {
      default = "${akka.serialization.DefaultEventAdapter.getClass.getName}"
    }

  event-adapter-bindings {
    ${eventWithSerializer map {
      case (event, eventSerializer) =>
        s""" 
           "${getClassName(event)}" = default 
        """
    } map ("         " + _) mkString "\n"}
  }
  """

}
