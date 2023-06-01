package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class Group(
  name: String,
  description: String,
  enrolmentPeriod: Period,
  enrolmentPolicy: String,
  minimumCapacity: Option[Int],
  maximumCapacity: Option[Int],
  idealCapacity: Option[Int],
  associatedCourses: List[AssociatedCourse],
  associatedGroups: List[AssociatedGroup]
) derives ConfiguredDecoder

case class AssociatedCourse(id: String, name: String, degrees: Seq[DegreeRef]) derives ConfiguredDecoder

case class AssociatedGroup(groupNumber: Int, shift: Option[String], members: Seq[Member]) derives ConfiguredDecoder

case class Member(name: String, username: String) derives ConfiguredDecoder



