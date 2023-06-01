package org.fenixedu.sdk.models

import io.circe.derivation.{Configuration, ConfiguredDecoder}
import io.circe.{Decoder, HCursor, Json}
import org.fenixedu.sdk.FenixEduClient

object SpaceRef:
  given Decoder[Option[SpaceRef]] = Decoder.decodeOption(summon[Decoder[SpaceRef]]).prepare(_.withFocus(_.withObject{ obj =>
    if obj("id").exists(_.isNull) then Json.Null
    else Json.fromJsonObject(obj)
  }))
case class SpaceRef(`type`: String, id: String, name: String) derives ConfiguredDecoder:
  def space[F[_]](implicit client: FenixEduClient[F]): F[Space] = client.spaces.get(id)

case class Capacity(normal: Int, exam: Int) derives ConfiguredDecoder

object Space:
  given Configuration = withDiscriminatorConfiguration
sealed trait Space derives ConfiguredDecoder:
  def id: String
  def name: String
  def containedSpaces: Seq[SpaceRef]

case class Campus(
  id: String,
  name: String,
  containedSpaces: List[SpaceRef],
) extends Space

case class Building(
  id: String,
  name: String,
  containedSpaces: List[SpaceRef],
  topLevelSpace: SpaceRef,
  parentSpace: SpaceRef,
) extends Space

case class Floor(
  id: String,
  name: String,
  containedSpaces: List[SpaceRef],
  topLevelSpace: SpaceRef,
  parentSpace: SpaceRef,
) extends Space

case class Room(
  id: String,
  name: String,
  containedSpaces: List[SpaceRef],
  topLevelSpace: SpaceRef,
  parentSpace: SpaceRef,
  description: String,
  capacity: Capacity,
  events: List[Events],
) extends Space
