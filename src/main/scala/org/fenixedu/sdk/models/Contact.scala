package org.fenixedu.sdk.models

import io.circe.derivation.ConfiguredDecoder

case class Contact(
  name: String,
  fax: String,
  phone: String,
  email: String,
  address: String,
  postalCode: String,
  workingHours: String,
) derives ConfiguredDecoder
