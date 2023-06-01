package org.fenixedu.sdk.services

import cats.effect.Concurrent
import cats.implicits.*
import org.fenixedu.sdk.models.{CourseRef, Degree}
import org.http4s.Uri
import org.http4s.client.Client

final class Degrees[F[_]: Concurrent](baseUri: Uri)(using client: Client[F]):
  val uri: Uri = baseUri / "degrees"

  /** @return the information for all degrees. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def list(academicTerm: Option[String] = None): F[List[Degree]] =
    client.expect(uri.withOptionQueryParam("academicTerm", academicTerm))

  /** @return the information for the `id` degree. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def get(id: String, academicTerm: Option[String] = None): F[Degree] =
    client.expect((uri / id).withOptionQueryParam("academicTerm", academicTerm))

  /** @return the information for the degree with `acronym` on the specified `academicTerm`.
    *         If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def getByAcronym(acronym: String, academicTerm: Option[String] = None): F[Option[Degree]] =
    // The API is not very expressive, so we need to do it using list.
    list(academicTerm).map(_.find(_.acronym == acronym))
  /** @return the information for the degree with `acronym` on the specified `academicTerm`, assuming the Degree exists.
    *         If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def applyByAcronym(acronym: String, academicTerm: Option[String] = None): F[Degree] =
    getByAcronym(acronym, academicTerm).flatMap {
      case Some(degree) => Concurrent[F].pure(degree)
      case None => Concurrent[F].raiseError(new NoSuchElementException(s"""Could not find Degree with acronym "$acronym"."""))
    }


  /** @return the information for a degree’s courses. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def courses(id: String, academicTerm: Option[String] = None): F[List[CourseRef]] =
    client.expect((uri / id / "courses").withOptionQueryParam("academicTerm", academicTerm))

  /** @return the information for the degree with `acronym` on the specified `academicTerm`.
    *         If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def getCoursesByAcronym(acronym: String, academicTerm: Option[String] = None): F[Option[List[CourseRef]]] =
    for
      courseOpt <- getByAcronym(acronym, academicTerm)
      courses <- courseOpt match
        case Some(degree) => courses(degree.id, academicTerm).map(Option.apply)
        case None => Concurrent[F].pure(Option.empty)
    yield courses

  /** @return the information for the degree with `acronym` on the specified `academicTerm`, assuming the Degree exists.
    *         If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def applyCoursesByAcronym(acronym: String, academicTerm: Option[String] = None): F[List[CourseRef]] =
    getCoursesByAcronym(acronym, academicTerm).flatMap {
      case Some(degree) => Concurrent[F].pure(degree)
      case None => Concurrent[F].raiseError(new NoSuchElementException(s"""Could not find Degree with acronym "$acronym"."""))
    }

