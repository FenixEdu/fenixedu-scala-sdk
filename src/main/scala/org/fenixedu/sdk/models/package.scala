package org.fenixedu.sdk.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import scala.util.Try
import io.circe.Decoder
import io.circe.derivation.Configuration

given configuration: Configuration = Configuration.default
val withDiscriminatorConfiguration: Configuration = configuration.withDiscriminator("type").withScreamingSnakeCaseConstructorNames

given Decoder[Locale] = Decoder.decodeString.emapTry(locale => Try(Locale.forLanguageTag(locale)))

def localDateTimeDecoderFromPattern(pattern: String): Decoder[LocalDateTime] =
  Decoder.decodeLocalDateTimeWithFormatter(DateTimeFormatter.ofPattern(pattern))
