package org.fenixedu.sdk.models

import io.circe.{Decoder, HCursor}
import io.circe.derivation.ConfiguredDecoder
import org.fenixedu.sdk.FenixEduClient
import org.http4s.Uri
import org.http4s.circe.decodeUri

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
) derives ConfiguredDecoder

case class Competence(
  id: String,
  program: Option[String],
  bibliographicReferences: List[BibliographicReference],
  degrees: List[DegreeRef],
) derives ConfiguredDecoder

object BibliographicReference:
  // Nuclear option to handle malformed URLs like these. We simply ignore them.
  //  "url" : "http://DOI 10.1007/978-3-642-28616-2 1"
  //  "url" : "http://fundamentals-of-bpm.org/  ;  ISBN: 978-3-642-33142-8 ;  DOI: 10.1007/978-3-642-33143-5"
  given Decoder[Option[Uri]] = Decoder.decodeOption(decodeUri).or((_: HCursor) => Right(None))
case class BibliographicReference(
  `type`: String,
  author: String,
  reference: String,
  title: String,
  year: String,
  url: Option[Uri],
) derives ConfiguredDecoder

case class CourseRef(
  id: String,
  acronym: String,
  name: String,
  academicTerm: String,
  url: Option[Uri],
) derives ConfiguredDecoder:
  def course[F[_]](using client: FenixEduClient[F]): F[Course] = client.course(id).model
