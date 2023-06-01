package org.fenixedu.sdk

import cats.effect.unsafe.implicits.global // I'm sure there is a better way to do this. Please do not copy paste
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import cats.effect.IO
import cats.instances.list.*
import cats.syntax.traverse.*
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.blaze.client.BlazeClientBuilder
import org.log4s.*
import org.scalatest.*
import org.scalatest.exceptions.TestFailedException
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.AsyncWordSpec

trait LowPriority:
  given Conversion[IO[Assertion], Future[Assertion]] = _.unsafeToFuture()

abstract class Utils extends AsyncWordSpec with Matchers with BeforeAndAfterAll with LowPriority:
  val logger: Logger = getLogger

  val (_httpClient, finalizer) = BlazeClientBuilder[IO]
    .withResponseHeaderTimeout(30.seconds)
    .withCheckEndpointAuthentication(false)
    .resource.allocated.unsafeRunSync()

  override protected def afterAll(): Unit =
    // For some reason this is returning java.lang.RuntimeException: TickWheelExecutor is shutdown
    finalizer.unsafeRunSync()

  given Client[IO] = _httpClient

  val client: FenixEduClient[IO] = new FenixEduClient(Uri.unsafeFromString("https://fenix.tecnico.ulisboa.pt/api/fenix/"))

  extension [T](io: IO[T])
    def value(test: T => Assertion): IO[Assertion] = io.map(test)

    def valueShouldBe(v: T): IO[Assertion] = value(_ shouldBe v)

    def idempotently(test: T => Assertion, repetitions: Int = 3): IO[Assertion] =
      require(repetitions >= 2, "To test for idempotency at least 2 repetitions must be made")
      io.flatMap { firstResult =>
        // If this fails we do not want to mask its exception with "Operation is not idempotent".
        // Because failing in the first attempt means whatever is being tested in `test` is not implemented correctly.
        test(firstResult)
        (2 to repetitions).toList.traverse { _ =>
          io
        } map { results =>
          // And now we want to catch the exception because if `test` fails here it means it is not idempotent.
          try
            results.foreach(test)
            succeed
          catch
            case e: TestFailedException =>
              val numberOfDigits = Math.floor(Math.log10(repetitions.toDouble)).toInt + 1
              val resultsString = (firstResult +: results).zipWithIndex
                .map { case (result, i) =>
                  s" %${numberOfDigits}d: %s".format(i + 1, result)
                }.mkString("\n")
              throw e.modifyMessage(_.map(message =>
                s"""Operation is not idempotent. Results:
                   |$resultsString
                   |$message""".stripMargin))
        }
      }

    def valueShouldIdempotentlyBe(value: T): IO[Assertion] = idempotently(_ shouldBe value)
