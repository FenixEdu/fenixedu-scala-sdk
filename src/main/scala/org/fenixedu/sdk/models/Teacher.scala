package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class Teacher(
  name: String,
  istId: String,
  mails: List[String],
  urls: List[String],
) derives ConfiguredDecoder
