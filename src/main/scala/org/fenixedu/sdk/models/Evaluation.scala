package org.fenixedu.sdk.models

import io.circe.derivation.{Configuration, ConfiguredDecoder}
import io.circe.{Decoder, HCursor}

object Evaluation:
  given config: Configuration = withDiscriminatorConfiguration
  given Decoder[Evaluation] = { (cursor: HCursor) =>
    val derivedDecoder = ConfiguredDecoder.derived[Evaluation]
    val downType = cursor.downField("type")
    downType.as[String].flatMap {
      case "EXAM" | "TEST" => derivedDecoder.elemDecoders.last.map(_.asInstanceOf[ExamOrTest]).tryDecode(cursor)
      case _ => derivedDecoder.tryDecode(cursor)
    }
  }
sealed trait Evaluation:
  def name: String
  def evaluationPeriod: Period
case class Project(name: String, evaluationPeriod: Period) extends Evaluation
case class OnlineTest(name: String, evaluationPeriod: Period) extends Evaluation
case class FinalEvaluation(name: String, evaluationPeriod: Period) extends Evaluation
case class AdHoc(name: String, evaluationPeriod: Period, description: Option[String]) extends Evaluation
case class ExamOrTest(
  id: String,
  name: String,
  evaluationPeriod: Period,
  enrollmentPeriod: Period,
  isInEnrolmentPeriod: Boolean,
  courses: List[CourseRef],
  rooms: List[SpaceRef],
  assignedRoom: Option[SpaceRef]
) extends Evaluation
