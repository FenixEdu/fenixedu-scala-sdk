package org.fenixedu.sdk.services

import cats.effect.Sync
import org.fenixedu.sdk.models.{CourseRef, Degree}
import org.http4s.Uri
import org.http4s.client.Client

final class Degrees[F[_]: Sync](baseUri: Uri)(implicit client: Client[F]) {
  val uri: Uri = baseUri / "degrees"

  /** @return the information for all degrees. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def list(academicTerm: Option[String] = None): F[List[Degree]] = client.expect(uri +??("academicTerm", academicTerm))

  /** @return the information for the `id` degree. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def get(id: String, academicTerm: Option[String] = None): F[Degree] = client.expect(uri / id +??("academicTerm", academicTerm))

  /** @return the information for a degree’s courses. If no academicTerm is defined it returns the degree information for the currentAcademicTerm. */
  def courses(id: String, academicTerm: Option[String] = None): F[List[CourseRef]] = client.expect(uri / id / "courses" +??("academicTerm", academicTerm))
}