package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class Schedule(lessonPeriods: List[Period], courseLoads: List[CourseLoad], shifts: List[Shift]) derives ConfiguredDecoder

case class CourseLoad(`type`: String, totalQuantity: Float, unitQuantity: Float) derives ConfiguredDecoder

case class Shift(name: String, occupation: Occupation, types: List[String], lessons: List[Lesson], rooms: List[SpaceRef]) derives ConfiguredDecoder

case class Occupation(current: Int, max: Int) derives ConfiguredDecoder

case class Lesson(start: String, end: String, room: Option[SpaceRef]) derives ConfiguredDecoder






