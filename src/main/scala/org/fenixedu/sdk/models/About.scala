package org.fenixedu.sdk.models

import java.util.Locale
import scala.util.Try
import io.circe.Decoder
import io.circe.derivation.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

object About {
  implicit val decoderLocale: Decoder[Locale] = Decoder.decodeString.emapTry(locale => Try(Locale.forLanguageTag(locale)))
  implicit val decoder: Decoder[About] = deriveDecoder(identity)
}
case class About(
  institutionName: String,
  institutionUrl: Uri,
  rssFeeds: List[RssFeeds],
  rss: Rss,
  currentAcademicTerm: String,
  languages: List[Locale],
  language: Locale
)

object Rss {
  implicit val decoder: Decoder[Rss] = deriveDecoder(identity)
}
case class Rss(news: Option[Uri], events: Option[Uri])

object RssFeeds {
  implicit val decoder: Decoder[RssFeeds] = deriveDecoder(identity)
}
case class RssFeeds(description: String, url: Uri)

