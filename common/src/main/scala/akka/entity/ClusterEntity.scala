package akka.entity

import akka.actor.{ActorRef, ActorSystem, Props}

trait ClusterEntity {

  def typeName: String = utils.Inference.getSimpleName(this.getClass.getName)

  def props: Props

  def start(implicit system: ActorSystem): ActorRef
}
