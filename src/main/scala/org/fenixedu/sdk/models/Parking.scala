package org.fenixedu.sdk.models

import java.time.LocalDateTime
import io.circe.Decoder
import io.circe.derivation.ConfiguredDecoder

object Parking:
  given Decoder[LocalDateTime] = localDateTimeDecoderFromPattern("yyyy-MM-dd HH:mm:ss")
case class Parking(
  name: String,
  description: String,
  campus: String,
  address: String,
  latlng: String,
  workingHours: String,
  total: Int,
  freeSlots: Int,
  updated: LocalDateTime,
) derives ConfiguredDecoder
