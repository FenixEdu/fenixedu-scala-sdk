package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.ConfiguredDecoder
import org.fenixedu.sdk.FenixEduClient
import org.http4s.Uri
import org.http4s.circe.decodeUri

object Degree:
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
  ) derives ConfiguredDecoder
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
) derives ConfiguredDecoder

case class DegreeRef(id: String, name: String, acronym: String) derives ConfiguredDecoder:
  def degree[F[_]](using client: FenixEduClient[F]): F[Degree] = client.degrees.get(id)
