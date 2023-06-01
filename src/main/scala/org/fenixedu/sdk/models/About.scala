package org.fenixedu.sdk.models

import java.util.Locale
import io.circe.derivation.ConfiguredDecoder
import org.http4s.Uri
import org.http4s.circe.decodeUri

case class About(
  institutionName: String,
  institutionUrl: Uri,
  rssFeeds: List[RssFeeds],
  rss: Rss,
  currentAcademicTerm: String,
  languages: List[Locale],
  language: Locale
) derives ConfiguredDecoder

case class Rss(news: Option[Uri], events: Option[Uri]) derives ConfiguredDecoder

case class RssFeeds(description: String, url: Uri) derives ConfiguredDecoder

