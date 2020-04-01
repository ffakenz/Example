package akka

import akka.actor.{Actor, ActorRef}

class ActorRefMap(newActor: String => ActorRef) extends collection.immutable.Map[String, ActorRef] {
  private var map = collection.immutable.Map.empty[String, ActorRef] // TODO why var

  override def apply(key: String): ActorRef = get(key) match {
    case None =>
      val typeName = key.replace(" ", "_")
      val ref = newActor(typeName)
      map = this.+((key, ref))
      ref
    case Some(ref) => ref
  }

  def get(key: String): Option[ActorRef] = {
    map.get(key)
  }

  def iterator: Iterator[(String, ActorRef)] = {
    map.iterator
  }

  override def removed(key: String): scala.collection.immutable.Map[String, akka.actor.ActorRef] = {
    map - key
  }
  override def updated[V1 >: akka.actor.ActorRef](key: String,
                                                  value: V1): scala.collection.immutable.Map[String, V1] = {
    map.+((key, value)) // (key,value)
  }
  /*override def removed(key : String):scala.collection.immutable.Map[String,akka.actor.ActorRef] = {
    map - key
  }

  override def updated[B1 >: ActorRef](kv: (String, B1)): Map[String, B1] = {
    map + kv
  }*/

}

object ActorRefMap {
  val empty = new ActorRefMap(_ => Actor.noSender)
}
