package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class Events(
  `type`: String,
  start: String,
  end: String,
  weekday: String,
  day: String,
  period: Period,
  description: Option[String],
  title: Option[String],
  info: Option[String],
  course: Option[CourseRef],
) derives ConfiguredDecoder
