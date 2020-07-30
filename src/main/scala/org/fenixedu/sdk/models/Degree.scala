package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder
import org.fenixedu.sdk.FenixEduClient
import org.http4s.Uri
import org.http4s.circe.decodeUri

object Degree {
  implicit val decoder: Decoder[Degree] = deriveDecoder(identity)

  object Info {
    implicit val decoder: Decoder[Info] = deriveDecoder(identity)
  }
  case class Info(
    description: Option[String],
    objectives: Option[String],
    designFor: Option[String],
    requisites: Option[String],
    profissionalExits: Option[String],
    history: Option[String],
    operationRegime: Option[String],
    gratuity: Option[String],
    links: Option[String]
  )
}
case class Degree(
  id: String,
  `type`: String,
  typeName: String,
  name: String,
  acronym: String,
  academicTerms: List[String],
  academicTerm: String,
  url: Option[Uri],
  campus: List[SpaceRef],
  info: Option[Degree.Info],
  teachers: List[Teacher]
)

object DegreeRef {
  implicit val decoder: Decoder[DegreeRef] = deriveDecoder(identity)
}
case class DegreeRef(id: String, name: String, acronym: String) {
  def degree[F[_]](implicit client: FenixEduClient[F]): F[Degree] = client.degrees.get(id)
}
