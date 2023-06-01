package org.fenixedu.sdk.models

import java.time.LocalDateTime
import io.circe.{Decoder, Json}

object Period:
  implicit val decoderLocalDateTime: Decoder[LocalDateTime] =
    localDateTimeDecoderFromPattern("dd/MM/yyyy HH:mm")
      .or(localDateTimeDecoderFromPattern("dd/MM/yyyy HH:mm:ss"))
      .or(localDateTimeDecoderFromPattern("yyyy-MM-dd HH:mm:ss"))

  implicit val decoder: Decoder[Period] =
    Decoder.forProduct2("start", "end")(Period.apply).prepare(_.withFocus(_.mapObject(_.mapValues(_.withString{
      case s if s.isEmpty => Json.Null
      case s => Json.fromString(s)
    }))))
case class Period(start: Option[LocalDateTime], end: Option[LocalDateTime])
