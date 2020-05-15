package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Evaluation {
  implicit val decoder: Decoder[Evaluation] = deriveDecoder(identity)
}

case class Evaluation(
  id: String,
  `type`: String,
  name: String,
  evaluationPeriod: Period,
  enrollmentPeriod: Period,
  isInEnrolmentPeriod: Boolean,
  courses: List[CourseRef],
  rooms: List[SpaceRef],
  assignedRoom: SpaceRef
)