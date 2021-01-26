package org.fenixedu.sdk

import java.time.Year
import cats.instances.list._
import cats.syntax.traverse._

class DegreeSpec extends Utils {
  "Degrees service" should {
    for (year <- 2000 until Year.now.getValue) {
      val academicTerm = s"$year/${year + 1}"
      s"list degrees of academic term $academicTerm" in {
        client.degrees.list(academicTerm = Some(academicTerm)).map(_.length should be > 1)
      }

      s"list degrees courses of academic term $academicTerm" in {
        client.degrees.list(academicTerm = Some(academicTerm))
          .flatMap(_.traverse(degree => client.degrees.courses(degree.id, Some(academicTerm))))
          .map { degreesCourses =>
            assert(degreesCourses.forall(_.length >= 0))
          }
      }
    }
  }
}