package org.fenixedu.sdk.services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import cats.effect.Concurrent
import fs2.Stream
import org.fenixedu.sdk.models.{Space, SpaceRef}
import org.http4s.Method.GET
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl

final class Spaces[F[_]: Concurrent](baseUri: Uri)(implicit client: Client[F]) {
  val uri: Uri = baseUri / "spaces"

  protected val dsl = new Http4sClientDsl[F] {}
  import dsl._

  /** @return returns the information about the campi. */
  def list(): F[List[SpaceRef]] = client.expect(uri)

  /** @return information about the space for a given `id`. The `id` can be for a Campus, Building, Floor or Room. */
  def get(id: String, day: Option[LocalDate] = None): F[Space] = {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    client.expect((uri / id).withOptionQueryParam("day", day.map(d => dateTimeFormatter.format(d))))
  }

  /** @return the space’s blueprint in the required format.*/
  def blueprint(id: String, format: Option[String] = Some("jpeg")): Stream[F, Byte] =
    client.stream(GET((uri / id / "blueprint").withOptionQueryParam("format", format))).flatMap(_.body)
}