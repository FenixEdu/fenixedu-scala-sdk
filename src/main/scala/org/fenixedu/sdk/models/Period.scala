package org.fenixedu.sdk.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.circe.{Decoder, Json}

object Period {
  private def forPattern(pattern: String): Decoder[LocalDateTime] =
    Decoder.decodeLocalDateTimeWithFormatter(DateTimeFormatter.ofPattern(pattern))

  implicit val decoderLocalDateTime: Decoder[LocalDateTime] =
    forPattern("dd/MM/yyyy HH:mm") or forPattern("dd/MM/yyyy HH:mm:ss") or forPattern("yyyy-MM-dd HH:mm:ss")

  implicit val decoder: Decoder[Period] =
    Decoder.forProduct2("start", "end")(Period.apply).prepare(_.withFocus(_.mapObject(_.mapValues(_.withString{
      case s if s.isEmpty => Json.Null
      case s => Json.fromString(s)
    }))))
}
case class Period(start: Option[LocalDateTime], end: Option[LocalDateTime])
