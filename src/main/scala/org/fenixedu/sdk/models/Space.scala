package org.fenixedu.sdk.models

import cats.effect.Sync
import io.circe.derivation.deriveDecoder
import io.circe.{Decoder, DecodingFailure, HCursor, Json}
import org.fenixedu.sdk.FenixEduClient

object SpaceRef {
  implicit val decoder: Decoder[SpaceRef] = deriveDecoder(identity)
  implicit val decoderOpt: Decoder[Option[SpaceRef]] = Decoder.decodeOption(decoder).prepare(_.withFocus(_.withObject{ obj =>
    if (obj("id").exists(_.isNull)) Json.Null
    else Json.fromJsonObject(obj)
  }))
}
case class SpaceRef(`type`: String, id: String, name: String) {
  def space[F[_]: Sync](implicit client: FenixEduClient[F]): F[Space] = client.spaces.get(id)
}

object Capacity {
  implicit val decoder: Decoder[Capacity] = deriveDecoder(identity)
}
case class Capacity(normal: Int, exam: Int)

object Space {
  implicit val decoder: Decoder[Space] = { cursor: HCursor =>
    val downType = cursor.downField("type")
    downType.as[String].flatMap {
      case "CAMPUS" => cursor.as[Campus]
      case "BUILDING" => cursor.as[Building]
      case "FLOOR" => cursor.as[Floor]
      case "ROOM" => cursor.as[Room]
      case t => Left(DecodingFailure(s"Unknown space type $t", downType.history))
    }
  }
}
sealed trait Space {
  def id: String
  def name: String
  def containedSpaces: Seq[SpaceRef]
}

object Campus {
  implicit val decoder: Decoder[Campus] = deriveDecoder(identity)
}
case class Campus(id: String, name: String, containedSpaces: List[SpaceRef]) extends Space

object Building {
  implicit val decoder: Decoder[Building] = deriveDecoder(identity)
}
case class Building(id: String, name: String, containedSpaces: List[SpaceRef], topLevelSpace: SpaceRef, parentSpace: SpaceRef) extends Space

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Floor {
  implicit val decoder: Decoder[Floor] = deriveDecoder(identity)
}
case class Floor(id: String, name: String, containedSpaces: List[SpaceRef], topLevelSpace: SpaceRef, parentSpace: SpaceRef) extends Space

object Room {
  implicit val decoder: Decoder[Room] = deriveDecoder(identity)
}
case class Room(id: String, name: String, containedSpaces: List[SpaceRef], topLevelSpace: SpaceRef, parentSpace: SpaceRef,
                description: String, capacity: Capacity, events: List[Events]) extends Space