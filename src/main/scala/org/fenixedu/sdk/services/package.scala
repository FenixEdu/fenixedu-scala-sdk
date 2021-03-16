package org.fenixedu.sdk

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder, Printer}
import org.http4s.{EntityDecoder, EntityEncoder, circe}

package object services {
  val jsonPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)
  implicit def jsonEncoder[F[_], A: Encoder]: EntityEncoder[F, A] = circe.jsonEncoderWithPrinterOf[F, A](jsonPrinter)
  implicit def jsonDecoder[F[_]: Concurrent, A: Decoder]: EntityDecoder[F, A] = circe.accumulatingJsonOf[F, A]

  // Without this decoding to Unit wont work. This makes the EntityDecoder[F, Unit] defined in EntityDecoder companion object
  // have a higher priority than the jsonDecoder defined above. https://github.com/http4s/http4s/issues/2806
  implicit def void[F[_]: Concurrent]: EntityDecoder[F, Unit] = EntityDecoder.void
}
