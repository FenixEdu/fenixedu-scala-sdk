package org.fenixedu.sdk.models

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter
import io.circe.Decoder
import io.circe.derivation.ConfiguredDecoder

case class Shuttle(stations: List[Station], date: List[TripDate], trips: List[Trip]) derives ConfiguredDecoder

case class Station(name: String, address: String, latlng: String) derives ConfiguredDecoder

object TripDate:
  given Decoder[LocalDate] = Decoder.decodeLocalDateWithFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
case class TripDate(start: LocalDate, end: LocalDate, `type`: String) derives ConfiguredDecoder

object Trip:
  object Stop:
    given Decoder[LocalTime] = Decoder.decodeLocalTimeWithFormatter(DateTimeFormatter.ofPattern("HH.mm"))
  case class Stop(station: String, hour: LocalTime) derives ConfiguredDecoder
case class Trip(`type`: String, stations: List[Trip.Stop]) derives ConfiguredDecoder
