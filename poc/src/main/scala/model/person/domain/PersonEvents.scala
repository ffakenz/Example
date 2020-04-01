package model.person.domain

sealed trait PersonEvents extends ddd.Event

object PersonEvents {
  case class PersonUpdatedFromChilds(aggregateRoot: String) extends PersonEvents

  case class PersonUpdatedStatus(aggregateRoot: String, status: String) extends PersonEvents

  case class PersonUpdatedStatusV2(aggregateRoot: String) extends PersonEvents
}
