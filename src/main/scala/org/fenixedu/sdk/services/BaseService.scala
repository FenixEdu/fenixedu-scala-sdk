package org.fenixedu.sdk.services

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import fs2.{Chunk, Stream}
import io.circe.{Decoder, HCursor}
import org.http4s.Method.GET
import org.http4s.circe.decodeUri
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Query, Request, Uri}

abstract class BaseService[F[_], T: Decoder](baseUri: Uri, val name: String)(implicit client: Client[F], F: Sync[F]) {
  protected val dsl = new Http4sClientDsl[F] {}
  import dsl._

  val pluralName = s"${name}s"
  val uri: Uri = baseUri / pluralName

  /** Takes a request and unwraps its return value. */
  protected def unwrap(request: F[Request[F]]): F[T] = client.expect[Map[String, T]](request).map(_.apply(name))
  /** Puts a value inside a wrapper. */
  protected def wrap(value: T): Map[String, T] = Map(name -> value)

  protected def genericList[R: Decoder](baseKey: String, uri: Uri, query: Query = Query.empty): Stream[F, R] = {
    implicit val paginatedDecoder: Decoder[(Option[Uri], List[R])] = (c: HCursor) => for {
      links <- c.downField("links").get[Option[Uri]]("next")
      objectList <- c.downField(baseKey).as[List[R]]
    } yield (links, objectList)

    Stream.unfoldChunkEval[F, Option[Uri], R](Some(uri)) {
      case Some(uri) =>
        for {
          request <- GET(uri.copy(query = uri.query ++ query.pairs))
          (next, entries) <- client.expect[(Option[Uri], List[R])](request)
        } yield Some((Chunk.iterable(entries), next))
      case None => F.pure(None)
    }
  }

  def list(): Stream[F, T] = genericList[T](pluralName, uri, Query.empty)
  def list(query: Query): Stream[F, T] = genericList[T](pluralName, uri, query)

  def get(id: String): F[T] = unwrap(GET(uri / id))
}