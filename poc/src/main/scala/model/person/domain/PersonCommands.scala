package model.person.domain

sealed trait PersonCommands extends ddd.Command

object PersonCommands {

  case class PersonUpdateStatus(aggregateRoot: String, deliveryId: BigInt, status: String) extends PersonCommands

  case class PersonUpdateFromChilds(aggregateRoot: String, deliveryId: BigInt) extends PersonCommands

}
