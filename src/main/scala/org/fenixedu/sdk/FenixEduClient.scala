package org.fenixedu.sdk

import cats.effect.Concurrent
import org.fenixedu.sdk.models.{Course => _, _}
import org.fenixedu.sdk.services._
import org.http4s.Uri
import org.http4s.client.Client

class FenixEduClient[F[_]](val baseUri: Uri)(implicit client: Client[F], F: Concurrent[F]) {
	val uri: Uri = baseUri / "v1"

	/** @return returns some basic information about the institution where the application is deployed.
		*         It also returns a list of RSS feeds, the current academic term, available languages and default language.*/
	def about: F[About] = client.expect(uri / "about")

	/** @return all the academic terms available to be used in other endpoints as academicTerm query parameter. */
	def academicTerms: F[Map[String, Array[String]]] = client.expect(uri / "academicterms")

	/** @return the menu information of Alameda’s canteen. */
	def canteen: F[List[DayMeals]] = client.expect(uri / "canteen")

	/** @return the contacts of campus, department, student residences, departments, IT services, etc */
	def contacts: F[List[Contact]] = client.expect(uri / "contacts")

	def course(id: String) = new Course[F](id, uri)
	val degrees = new Degrees[F](uri)

	/** @return the number of free parking slots for each parking location. */
	def parking: F[Map[String, Parking]] = client.expect(uri / "parking")

	/** @return the shuttle information, including stops and timetables. */
	def shuttle: F[Shuttle] = client.expect(uri / "shuttle")

	val spaces = new Spaces[F](uri)
}
