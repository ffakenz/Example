package akka.entity

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

trait ShardedEntity[Requirements] extends ClusterEntity[Requirements] {

  import ShardedEntity._

  def props(requirements: Requirements): Props

  def start(requirements: Requirements)(
      implicit
      system: ActorSystem
  ): ActorRef = ClusterSharding(system).start(
    typeName = typeName,
    entityProps = props(requirements),
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
      scribe.debug(s"Sharded $s to $sharded")
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
