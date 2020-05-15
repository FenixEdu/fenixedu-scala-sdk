package org.fenixedu.sdk.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Period {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
  implicit val decoderLocalDateTime: Decoder[LocalDateTime] = Decoder.decodeLocalDateTimeWithFormatter(dateTimeFormatter)
  implicit val decoder: Decoder[Period] = deriveDecoder(identity)
}
case class Period(start: Option[LocalDateTime], end: Option[LocalDateTime])
