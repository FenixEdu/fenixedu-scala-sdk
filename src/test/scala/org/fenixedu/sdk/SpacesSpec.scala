package org.fenixedu.sdk

import cats.effect.IO
import cats.instances.list._
import cats.syntax.traverse._
import org.fenixedu.sdk.models.{Building, Campus, Floor, Room, Space}
import org.scalatest.Succeeded
import org.scalatest.exceptions.TestFailedException

class SpacesSpec extends Utils {
  val campiAsSpaces: IO[List[Space]] = client.spaces.list().flatMap(_.traverse(campus => client.spaces.get(campus.id)))
  val campi: IO[List[Campus]] = campiAsSpaces.map(_.collect{ case c: Campus => c })

  def getContainedSpaces(campus: Campus): IO[List[Space]] =
    campus.containedSpaces.traverse(space => client.spaces.get(space.id))

  "Spaces service" should {
    "list spaces" in {
      campiAsSpaces.idempotently(_ should have length 3)
    }
    "show details about each campus" in {
      campiAsSpaces.idempotently { campus =>
        assert(campus.forall(_.isInstanceOf[Campus]))
      }
    }
    "show details about each campi contained spaces" in {
      campi.flatMap { campi =>
        campi.flatTraverse(getContainedSpaces).map { spaces =>
          spaces.map {
            case Building(_, _, containedSpaces, _, parentSpace) =>
              parentSpace.`type` shouldBe "CAMPUS"
              containedSpaces.count(_.`type` == "FLOOR") should be >= 0
              containedSpaces.count(_.`type` == "ROOM") should be >= 0
            case Floor(_, _, containedSpaces, _, parentSpace) =>
              parentSpace.`type` shouldBe "BUILDING"
              containedSpaces.map(_.`type`) should contain ("ROOM")
            case Room(_, _, containedSpaces, _, _, _, _, _) =>
              containedSpaces.map(_.`type`) should contain only "ROOM"
            case _: Campus => throw new TestFailedException("A campus cannot contain campus", 2)
          }
        }
      } map (_ should contain only Succeeded) // Scalatest flatten :P
    }
  }
}