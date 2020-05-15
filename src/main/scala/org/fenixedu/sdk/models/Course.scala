package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

object Course {
  implicit val decoder: Decoder[Course] = deriveDecoder(identity)
}
case class Course(
  acronym: String,
  name: String,
  academicTerm: String,
  evaluationMethod: String,
  numberOfAttendingStudents: Int,
  summaryLink: String,
  announcementLink: String,
  url: Uri,
  competences: List[Competence],
  teachers: List[Teacher]
)

object Competence {
  implicit val decoder: Decoder[Competence] = deriveDecoder(identity)
}
case class Competence(id: String, program: String, bibliographicReferences: List[BibliographicReference], degrees: List[DegreeRef])

object BibliographicReference {
  implicit val decoder: Decoder[BibliographicReference] = deriveDecoder(identity)
}
case class BibliographicReference(`type`: String, author: String, reference: String, title: String, year: String, url: Uri)

object CourseLoad {
  implicit val decoder: Decoder[CourseLoad] = deriveDecoder(identity)
}
case class CourseLoad(`type`: String, totalQuantity: Int, unitQuantity: Int)

object CourseRef {
  implicit val decoder: Decoder[CourseRef] = deriveDecoder(identity)
}
case class CourseRef(id: String, acronym: String, name: String, academicTerm: String, url: Option[Uri])