package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object AttendingStudents {
  implicit val decoder: Decoder[AttendingStudents] = deriveDecoder(identity)
}
case class AttendingStudents(enrolmentCount: Int, attendingCount: Int, students: List[Student])

object Student {
  implicit val decoder: Decoder[Student] = deriveDecoder(identity)
}
case class Student(username: String, degree: DegreeRef)

