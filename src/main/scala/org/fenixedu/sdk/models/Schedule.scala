package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Schedule {
  implicit val decoder: Decoder[Schedule] = deriveDecoder(identity)
}
case class Schedule(lessonPeriods: List[Period], courseLoads: List[CourseLoad], shifts: List[Shift])

object CourseLoad {
  implicit val decoder: Decoder[CourseLoad] = deriveDecoder(identity)
}
case class CourseLoad(`type`: String, totalQuantity: Float, unitQuantity: Float)

object Shift {
  implicit val decoder: Decoder[Shift] = deriveDecoder(identity)
}
case class Shift(name: String, occupation: Occupation, types: List[String], lessons: List[Lesson], rooms: List[SpaceRef])

object Occupation {
  implicit val decoder: Decoder[Occupation] = deriveDecoder(identity)
}
case class Occupation(current: Int, max: Int)

object Lesson {
  implicit val decoder: Decoder[Lesson] = deriveDecoder(identity)
}
case class Lesson(start: String, end: String, room: Option[SpaceRef])






