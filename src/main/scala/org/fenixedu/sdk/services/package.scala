package org.fenixedu.sdk.services

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder, Printer}
import org.http4s.{EntityDecoder, EntityEncoder, circe}

val jsonPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)
given [F[_], A: Encoder]: EntityEncoder[F, A] = circe.jsonEncoderWithPrinterOf(jsonPrinter)
given [F[_]: Concurrent, A: Decoder]: EntityDecoder[F, A] = circe.accumulatingJsonOf

// Without this decoding to Unit wont work. This makes the EntityDecoder[F, Unit] defined in EntityDecoder companion object
// have a higher priority than the jsonDecoder defined above. https://github.com/http4s/http4s/issues/2806
given [F[_]: Concurrent]: EntityDecoder[F, Unit] = EntityDecoder.void
