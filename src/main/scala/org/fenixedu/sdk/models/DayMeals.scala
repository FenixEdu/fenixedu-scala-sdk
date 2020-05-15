package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object DayMeals {
  implicit val decoder: Decoder[DayMeals] = deriveDecoder(identity)
}
case class DayMeals(day: String, meal: List[Meal])

object Meal {
  implicit val decoder: Decoder[Meal] = deriveDecoder(identity)

  object Info {
    implicit val decoder: Decoder[Info] = deriveDecoder(identity)
  }
  case class Info(`type`: String, menu: String, name: String)
}
case class Meal(info: List[Meal.Info], `type`: String)

