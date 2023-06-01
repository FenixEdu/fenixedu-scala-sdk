package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class AttendingStudents(enrolmentCount: Int, attendingCount: Int, students: List[Student]) derives ConfiguredDecoder

case class Student(username: String, degree: DegreeRef) derives ConfiguredDecoder
