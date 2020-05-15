package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Group{
  implicit val decoder: Decoder[Group] = deriveDecoder(identity)
}
case class Group(
  name: String,
  description: String,
  enrolmentPeriod: Period,
  enrolmentPolicy: String,
  minimumCapacity: Int,
  maximumCapacity: Int,
  idealCapacity: Int,
  associatedCourses: List[AssociatedCourse],
  associatedGroups: List[AssociatedGroup]
)

object AssociatedCourse {
  implicit val decoder: Decoder[AssociatedCourse] = deriveDecoder(identity)
}
case class AssociatedCourse(id: String, name: String, degrees: Seq[DegreeRef])

object AssociatedGroup{
  implicit val decoder: Decoder[AssociatedGroup] = deriveDecoder (identity)
}
case class AssociatedGroup(groupNumber: Int, shift: String, members: Seq[Member])

object Member {
  implicit val decoder: Decoder[Member] = deriveDecoder(identity)
}
case class Member(name: String, username: String)



