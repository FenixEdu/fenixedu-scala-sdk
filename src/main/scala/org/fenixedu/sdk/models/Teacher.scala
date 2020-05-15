package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Teacher {
  implicit val decoder: Decoder[Teacher] = deriveDecoder(identity)
}
case class Teacher(name: String, istId: String, mails: List[String], urls: List[String])
