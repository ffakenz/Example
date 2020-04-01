import akka.entity.ShardedEntity.Sharded
import utils.Inference.getSimpleName


package object ddd {

  trait Deliverable {
    def deliveryId: BigInt
  }

  sealed trait Aggregate {
    def aggregateRoot: String
  }

  sealed trait ShardedMessage extends Aggregate with Sharded

  trait Command extends ShardedMessage with Deliverable {
    def entityId: String = aggregateRoot
    def shardedId: String = aggregateRoot + entityId
  }


  trait Event extends Aggregate {
    def name: String = getSimpleName(this.getClass.getName)
  }

  trait AbstractState[Event] {
    def +(e: Event): AbstractState[Event]
  }

}
