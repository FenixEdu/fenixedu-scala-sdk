package org.fenixedu.sdk.models

import io.circe.derivation.deriveDecoder
import io.circe.{Decoder, DecodingFailure, HCursor}

object Evaluation {
  implicit val decoderProject: Decoder[Project] = deriveDecoder(identity)
  implicit val decoderOnlineTest: Decoder[OnlineTest] = deriveDecoder(identity)
  implicit val decoderFinalEvaluation: Decoder[FinalEvaluation] = deriveDecoder(identity)
  implicit val decoderAdHoc: Decoder[AdHoc] = deriveDecoder(identity)
  implicit val decoderExamOrTest: Decoder[ExamOrTest] = deriveDecoder(identity)

  implicit val decoder: Decoder[Evaluation] = { cursor: HCursor =>
    val downType = cursor.downField("type")
    downType.as[String].flatMap {
      case "PROJECT" => cursor.as[Project]
      case "ONLINE_TEST" => cursor.as[OnlineTest]
      case "FINAL_EVALUATION" => cursor.as[FinalEvaluation]
      case "AD_HOC" => cursor.as[AdHoc]
      case "EXAM" | "TEST" => cursor.as[ExamOrTest]
      case t => Left(DecodingFailure(s"Unknown evaluation type $t", downType.history))
    }
  }
}
sealed trait Evaluation {
  def `type`: String
  def name: String
  def evaluationPeriod: Period
}
case class Project(name: String, evaluationPeriod: Period) extends Evaluation {
  final val `type`: String = "PROJECT"
}
case class OnlineTest(name: String, evaluationPeriod: Period) extends Evaluation {
  final val `type`: String = "ONLINE_TEST"
}
case class FinalEvaluation(name: String, evaluationPeriod: Period) extends Evaluation {
  final val `type`: String = "FINAL_EVALUATION"
}
case class AdHoc(name: String, evaluationPeriod: Period, description: Option[String]) extends Evaluation {
  final val `type`: String = "AD_HOC"
}
case class ExamOrTest(
  id: String,
  `type`: String,
  name: String,
  evaluationPeriod: Period,
  enrollmentPeriod: Period,
  isInEnrolmentPeriod: Boolean,
  courses: List[CourseRef],
  rooms: List[SpaceRef],
  assignedRoom: Option[SpaceRef]
) extends Evaluation