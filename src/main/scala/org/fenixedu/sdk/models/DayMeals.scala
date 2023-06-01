package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class DayMeals(day: String, meal: List[Meal]) derives ConfiguredDecoder

object Meal:
  case class Info(`type`: String, menu: String, name: String) derives ConfiguredDecoder
case class Meal(info: List[Meal.Info], `type`: String) derives ConfiguredDecoder

