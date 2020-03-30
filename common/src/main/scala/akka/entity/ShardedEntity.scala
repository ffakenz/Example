package akka.entity

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

trait ShardedEntity extends ClusterEntity {

  import ShardedEntity._

  def props: Props

  def start(
      implicit
      system: ActorSystem
  ): ActorRef = ClusterSharding(system).start(
    typeName = typeName,
    entityProps = props,
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId(3)
  )
}

object ShardedEntity {
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case s: Sharded => (s.entityId, s)
  }

  def extractShardId(numberOfShards: Int): ShardRegion.ExtractShardId = {
    case s: Sharded =>
      val sharded = (s.shardedId.hashCode % numberOfShards).toString
      scribe.info(s"Sending $s to node $sharded")
      sharded

  }

  case class NoRequirements()
  case class ActorRefRequirement(actorRef: ActorRef)

  trait Sharded {
    def entityId: String
    def shardedId: String
    def tupled = (entityId, shardedId)
  }

}
