package org.fenixedu.sdk.models

import io.circe.Decoder
import io.circe.derivation.deriveDecoder

object Contact {
  implicit val decoder: Decoder[Contact] = deriveDecoder(identity)
}
case class Contact(name: String, fax: String, phone: String, email: String, address: String, postalCode: String, workingHours: String)