organization := "org.fenixedu"
name := "fenixedu-scala-sdk"

// ======================================================================================================================
// ==== Compile Options =================================================================================================
// ======================================================================================================================
javacOptions ++= Seq("-Xlint", "-encoding", "UTF-8", "-Dfile.encoding=utf-8")
scalaVersion := "2.13.5"

scalacOptions ++= Seq(
  "-encoding", "utf-8",            // Specify character encoding used by source files.
  "-explaintypes",                 // Explain type errors in more detail.
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
  "-Ybackend-parallelism", "8",    // Maximum worker threads for backend.
  "-Ybackend-worker-queue", "10",  // Backend threads worker queue size.
  "-Ymacro-annotations",           // Enable support for macro annotations, formerly in macro paradise.
  "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
  "-Xsource:3",                    // Treat compiler input as Scala source for the specified version.
  "-Xmigration:3",                 // Warn about constructs whose behavior may have changed since version.
  "-Werror",                       // Fail the compilation if there are any warnings.
  "-Xlint:_",                      // Enables every warning. scalac -Xlint:help for a list and explanation
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-Wdead-code",                   // Warn when dead code is identified.
  "-Wextra-implicit",              // Warn when more than one implicit parameter section is defined.
  "-Wnumeric-widen",               // Warn when numerics are widened.
  //"-Woctal-literal",               // Warn on obsolete octal syntax.
  "-Wvalue-discard",               // Warn when non-Unit expression results are unused.
  "-Wunused:_",                    // Enables every warning of unused members/definitions/etc
)

// These lines ensure that in sbt console or sbt test:console the -Ywarn* and the -Xfatal-warning are not bothersome.
// https://stackoverflow.com/questions/26940253/in-sbt-how-do-you-override-scalacoptions-for-console-in-all-configurations
Compile / console / scalacOptions ~= (_.filterNot { option =>
  option.startsWith("-W") || option.startsWith("-Xlint")
})
Test / console / scalacOptions := (Test / console / scalacOptions).value

console / initialCommands :=
  """import scala.concurrent.ExecutionContext
    |import java.util.concurrent.Executors
    |import cats.effect.{Blocker, ContextShift, IO}
    |import org.http4s.client.{Client, JavaNetClientBuilder}
    |import org.http4s.syntax.all._
    |import org.fenixedu.sdk.FenixEduClient
    |import org.fenixedu.sdk.services._
    |
    |val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
    |implicit val cs: ContextShift[IO] = IO.contextShift(ec)
    |implicit val httpClient: Client[IO] = JavaNetClientBuilder[IO](Blocker.liftExecutionContext(ec)).create
    |val client: FenixEduClient[IO] = new FenixEduClient[IO](uri"https://fenix.tecnico.ulisboa.pt/api/fenix/")""".stripMargin

// ======================================================================================================================
// ==== Dependencies ====================================================================================================
// ======================================================================================================================
libraryDependencies ++= Seq("blaze-client", "circe").map { module =>
  "org.http4s"      %% s"http4s-$module" % "1.0.0-M21"
} ++ Seq(
  "io.circe"        %% "circe-derivation"  % "0.13.0-M5",
  "io.circe"        %% "circe-parser"      % "0.13.0",
  "com.beachape"    %% "enumeratum-circe"  % "1.6.1",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3" % Test,
  "org.scalatest"   %% "scalatest"         % "3.2.7" % Test,
)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

// ======================================================================================================================
// ==== Testing =========================================================================================================
// ======================================================================================================================
// By default, logging is buffered for each test source file until all tests for that file complete. This disables it.
Test / logBuffered := false
// By default, tests executed in a forked JVM are executed sequentially.
Test / fork := true
// So we make them run in parallel.
Test / testForkedParallel := true
// The tests in a single group are run sequentially. We run 4 suites per forked VM.
Test / testGrouping := {
  import sbt.Tests.{Group, SubProcess}
  (Test / definedTests).value.grouped(4).zipWithIndex.map { case (tests, index) =>
    Group(index.toString, tests, SubProcess(ForkOptions()))
  }.toSeq
}

// http://www.scalatest.org/user_guide/using_the_runner
//   -o[configs...] - causes test results to be written to the standard output. The D configs shows all durations.
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// ======================================================================================================================
// ==== Scaladoc ========================================================================================================
// ======================================================================================================================
git.remoteRepo := s"git@github.com:FenixEdu/${name.value}.git"
git.useGitDescribe := true // Get version by calling `git describe` on the repository
val latestReleasedVersion = SettingKey[String]("latest released version")
latestReleasedVersion := git.gitDescribedVersion.value.getOrElse("0.0.1-SNAPSHOT")

// Define the base URL for the Scaladocs for your library. This will enable clients of your library to automatically
// link against the API documentation using autoAPIMappings.
apiURL := Some(url(s"${homepage.value.get}/api/${latestReleasedVersion.value}/"))
autoAPIMappings := true // Tell scaladoc to look for API documentation of managed dependencies in their metadata.
Compile / doc / scalacOptions ++= Seq(
  "-author",      // Include authors.
  "-diagrams",    // Create inheritance diagrams for classes, traits and packages.
  "-groups",      // Group similar functions together (based on the @group annotation)
  "-implicits",   // Document members inherited by implicit conversions.
  "-doc-title", name.value.capitalize,
  "-doc-version", latestReleasedVersion.value,
  "-doc-source-url", s"${homepage.value.get}/tree/v${latestReleasedVersion.value}€{FILE_PATH}.scala",
  "-sourcepath", baseDirectory.value.getAbsolutePath,
)

enablePlugins(GhpagesPlugin, SiteScaladocPlugin)
SiteScaladoc / siteSubdirName := s"api/${version.value}"
ghpagesCleanSite / excludeFilter := AllPassFilter // We want to keep all the previous API versions
val latestFileName = "latest"
val createLatestSymlink = taskKey[Unit](s"Creates a symlink named $latestFileName which points to the latest version.")
createLatestSymlink := {
  ghpagesSynchLocal.value // Ensure the ghpagesRepository already exists
  import java.nio.file.Files
  val path = (ghpagesRepository.value / "api" / latestFileName).toPath
  if (!Files.isSymbolicLink(path)) Files.createSymbolicLink(path, new File(latestReleasedVersion.value).toPath)
}
ghpagesPushSite := ghpagesPushSite.dependsOn(createLatestSymlink).value
ghpagesBranch := "gh-pages"
ghpagesNoJekyll := false
ghpagesPushSite / envVars := Map("SBT_GHPAGES_COMMIT_MESSAGE" -> s"Add Scaladocs for version ${latestReleasedVersion.value}")

// ======================================================================================================================
// ==== Publishing/Release ==============================================================================================
// ======================================================================================================================
publishTo := sonatypePublishTo.value
licenses += "MIT" -> url("http://opensource.org/licenses/MIT")
homepage := Some(url(s"https://github.com/FenixEdu/${name.value}"))
scmInfo := Some(ScmInfo(homepage.value.get, git.remoteRepo.value))
developers ++= List(
  Developer("Lasering", "Simão Martins", "", url("https://github.com/Lasering")),
)

// Fail the build/release if there are updates for the dependencies
dependencyUpdatesFailBuild := true

// By default the version is set on the build level (using version in ThisBuild).
// When this is set to false, version like version := "1.2.3" will be written to version.sbt.
releaseUseGlobalVersion := false
releaseNextCommitMessage := s"Setting version to ${ReleasePlugin.runtimeVersion.value} [skip ci]"

releasePublishArtifactsAction := PgpKeys.publishSigned.value // Maven Central requires packages to be signed
import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  releaseStepTask(dependencyUpdates),
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepTask(Compile / doc),
  releaseStepTask(Test / test),
  setReleaseVersion,
  tagRelease,
  releaseStepTask(ghpagesPushSite),
  publishArtifacts,
  releaseStepCommand("sonatypeRelease"),
  pushChanges,
  setNextVersion
)