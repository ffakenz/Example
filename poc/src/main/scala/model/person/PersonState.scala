package model.person

import java.time.LocalDateTime

import ddd._
import model.person.domain.{PersonEvents}

final case class PersonState(status: String = "Buying stuff in the market!") extends AbstractState[PersonEvents] {
  def +(event: PersonEvents): PersonState = event match {
    case evt: PersonEvents.PersonUpdatedFromChilds =>
      this

    case evt: PersonEvents.PersonUpdatedStatus =>
      copy(status = evt.status)

    case evt: PersonEvents.PersonUpdatedStatusV2 =>
      this
  }
}
