package org.fenixedu.sdk.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Parking {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  implicit val decoderLocalDateTime: Decoder[LocalDateTime] = Decoder.decodeLocalDateTimeWithFormatter(dateTimeFormatter)
  implicit val decoder: Decoder[Parking] = deriveDecoder(identity)
}
case class Parking(name: String, description: String, campus: String, address: String, latlng: String, workingHours: String, total: Int, freeSlots: Int, updated: LocalDateTime)