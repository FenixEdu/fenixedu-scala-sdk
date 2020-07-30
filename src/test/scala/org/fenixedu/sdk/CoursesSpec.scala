package org.fenixedu.sdk

import cats.instances.list._
import cats.syntax.parallel._
import org.http4s.Status.InternalServerError
import org.http4s.client.UnexpectedStatus

class CoursesSpec extends Utils {
  def forAcademicTerm(year: Int) = {
    val academicTerm = s"$year/${year + 1}"
    client.degrees.list(academicTerm = Some(academicTerm))
      .flatMap(_.parTraverse(degree => client.degrees.courses(degree.id, Some(academicTerm)).map(courses => (degree, courses.take(3)))))
      .unsafeRunSync()
      .foreach {  case (degree, courses) =>
        s"the degree is ${degree.acronym} (${degree.id}) on academic term $academicTerm" should {
          courses.foreach { courseRef =>
            val courseService = client.course(courseRef.id)
            s"get the course ${courseRef.acronym} (${courseRef.id})" in {
              courseService.get().value(_.academicTerm should include (academicTerm))
            }
            s"get the course ${courseRef.acronym} (${courseRef.id}) evaluations" in {
              courseService.evaluations.value(_.length should be >= 0)
            }
            s"get the course groups ${courseRef.acronym} (${courseRef.id})" in {
              courseService.groups.value(_.length should be >= 0)
            }
            s"get the course schedule ${courseRef.acronym} (${courseRef.id})" in {
              courseService.schedule.value(_.lessonPeriods.length should be >= 0).recover {
                case UnexpectedStatus(_: InternalServerError.type) =>
                  // There is an error on the server, since we cannot easily exclude the Courses with the error we simply ignore it
                  assert(true)
              }
            }
            s"get the course students ${courseRef.acronym} (${courseRef.id})" in {
              courseService.students.value(_.students.length should be >= 0)
            }
          }
        }
      }
  }

  "Courses service" when {
    // These tests take very long. So we run them for just some years, it probably covers most scenarios.
    forAcademicTerm(2000) // 0 tests, all the Degrees from this year have 0 courses. I don't know why.
    // From 2002 to 2007 the API returns degrees with the same id
    forAcademicTerm(2007) // 1205 tests
    forAcademicTerm(2019) // 1155 tests
    forAcademicTerm(2020) // As of 18/05/2020 0 tests
  }
}