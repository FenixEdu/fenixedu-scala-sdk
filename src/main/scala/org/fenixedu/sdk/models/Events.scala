package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Events {
  implicit val decoder: Decoder[Events] = deriveDecoder(identity)
}
case class Events(`type`: String, start: String, end: String, weekday: String, day: String, period: Period, description: Option[String],
                  title: Option[String], info: Option[String], course: Option[CourseRef])
