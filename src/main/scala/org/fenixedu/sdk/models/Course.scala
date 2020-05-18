package org.fenixedu.sdk.models

import cats.effect.Sync
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.circe.derivation.deriveDecoder
import org.fenixedu.sdk.FenixEduClient
import org.http4s.Uri
import org.http4s.circe.decodeUri

object Course {
  implicit val decoder: Decoder[Course] = deriveDecoder(identity)
}
case class Course(
  acronym: String,
  name: String,
  academicTerm: String,
  evaluationMethod: Option[String],
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
case class Competence(id: String, program: Option[String], bibliographicReferences: List[BibliographicReference], degrees: List[DegreeRef])

object BibliographicReference {
  // Nuclear option to handle malformed URLs like these. We simply ignore them.
  //  "url" : "http://DOI 10.1007/978-3-642-28616-2 1"
  //  "url" : "http://fundamentals-of-bpm.org/  ;  ISBN: 978-3-642-33142-8 ;  DOI: 10.1007/978-3-642-33143-5"
  implicit val decoderOptionUri: Decoder[Option[Uri]] = Decoder.decodeOption[Uri](decodeUri) or ((_: HCursor) => Right(None))
  implicit val decoder: Decoder[BibliographicReference] = deriveDecoder(identity)
}
case class BibliographicReference(`type`: String, author: String, reference: String, title: String, year: String, url: Option[Uri])

object CourseRef {
  implicit val decoder: Decoder[CourseRef] = deriveDecoder(identity)
}
case class CourseRef(id: String, acronym: String, name: String, academicTerm: String, url: Option[Uri]) {
  def course[F[_]: Sync](implicit client: FenixEduClient[F]): F[Course] = client.course(id).get()
}