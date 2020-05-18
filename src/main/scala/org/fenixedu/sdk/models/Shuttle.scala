package org.fenixedu.sdk.models

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter
import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Shuttle {
  implicit val decoder: Decoder[Shuttle] = deriveDecoder(identity)
}
case class Shuttle(stations: List[Station], date: List[TripDate], trips: List[Trip])

object Station {
  implicit val decoder: Decoder[Station] = deriveDecoder(identity)
}
case class Station(name: String, address: String, latlng: String)

object TripDate {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  implicit val decoderLocalDate: Decoder[LocalDate] = Decoder.decodeLocalDateWithFormatter(dateTimeFormatter)
  implicit val decoder: Decoder[TripDate] = deriveDecoder(identity)
}
case class TripDate(start: LocalDate, end: LocalDate, `type`: String)

object Trip {
  object Stop {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH.mm")
    implicit val decoderLocalTime: Decoder[LocalTime] = Decoder.decodeLocalTimeWithFormatter(dateTimeFormatter)
    implicit val decoder: Decoder[Stop] = deriveDecoder(identity)
  }
  case class Stop(station: String, hour: LocalTime)

  implicit val decoder: Decoder[Trip] = deriveDecoder(identity)
}
case class Trip(`type`: String, stations: List[Trip.Stop])
