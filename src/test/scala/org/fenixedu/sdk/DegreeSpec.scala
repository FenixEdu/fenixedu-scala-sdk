package org.fenixedu.sdk

import cats.instances.list.*
import cats.syntax.traverse.*

class DegreeSpec extends Utils:
  "Degrees service" should {
    for (year <- 2020 until 2004 by -2)
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
