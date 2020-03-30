import akka.entity.ShardedEntity.Sharded

package object ddd {

  trait Deliverable {
    def deliveryId: BigInt
  }

  sealed trait Aggregate {
    def aggregateRoot: String
  }

  sealed trait ShardedMessage extends Aggregate with Sharded

  trait Command extends ShardedMessage with Deliverable {
    override def entityId: String = aggregateRoot
    override def shardedId: String = aggregateRoot
  }

  trait Event
}
