package org.fenixedu.sdk.services

import cats.effect.Sync
import org.fenixedu.sdk.models.{AttendingStudents, Evaluation, Group, Schedule, Course => CouseModel}
import org.http4s.Uri
import org.http4s.client.Client

final class Course[F[_]: Sync](val id: String, baseUri: Uri)(implicit client: Client[F]) {
  val uri: Uri = baseUri / "courses" / id

  /** A course is a concrete unit of teaching that typically lasts one academic term.
    * This endpoint shows some information regarding a particular course.
    * The same course may be lectured simultaneously in multiple degrees during the same academic term.
    * The “competences” field holds curricular information for each set of degrees in which the course is lectured.
    * Usually this information is the same for all the associated degrees. */
  def apply(): F[CouseModel] = client.expect(uri)

  /** An evaluation is a component of a course in which the teacher determines the extent of the students understanding of the program.
    * Current known implementations of evaluations are: tests, exams, projects, online tests and ad-hoc evaluations. */
  val evaluations: F[List[Evaluation]] = client.expect(uri / "evaluations")

  /** Groups are used in courses for a wide range of purposes. The most typical are for creating teams of students for laboratories or projects.
    * Some groups are shared among different courses. The enrolment of student groups may be atomic or individual, and may be restricted to an enrolment period. */
  val groups: F[List[Group]] = client.expect(uri / "groups")

  /** Each course is lectured during a specific set of intervals. These intervals make up the lesson period for that course.
    * Each course also has a curricular load that specifies the time each student will expend with the course. Each shift is the possible
    * schedule in which a student should enrol. */
  val schedule: F[List[Schedule]] = client.expect(uri / "schedule")

  /** This endpoint lists all the students attending the specified course. For each student it indicates the corresponding degree.
    * The endpoint also returns the number of students officially enrolled in the course. */
  val students: F[AttendingStudents] = client.expect(uri / "students")
}
