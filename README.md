# fenixedu-scala-sdk [![license](http://img.shields.io/:license-MIT-blue.svg)](LICENSE)
[![Scaladoc](http://javadoc-badge.appspot.com/org.fenixedu/fenixedu-scala-sdk_2.13.svg?label=scaladoc&style=plastic&maxAge=604800)](https://fenixedu.github.io/fenixedu-scala-sdk/api/latest/org/fenixedu/sdk/FenixEduClient.html)
[![Latest version](https://index.scala-lang.org/FenixEdu/fenixedu-scala-sdk/fenixedu-scala-sdk/latest.svg)](https://index.scala-lang.org/FenixEdu/fenixedu-scala-sdk/fenixedu-scala-sdk)

[![Build Status](https://travis-ci.org/FenixEdu/fenixedu-scala-sdk.svg?branch=master&style=plastic&maxAge=604800)](https://travis-ci.org/FenixEdu/fenixedu-scala-sdk)

A pure functional Scala client for the FenixEdu API implemented using Http4s client.

Only the public endpoints are supported.

[Latest scaladoc documentation](https://fenixedu.github.io/fenixedu-scala-sdk/latest/api/org/fenixedu/sdk/index.html)

## Install
Add the following dependency to your `build.sbt`:
```sbt
libraryDependencies += "org.fenixedu" %% "fenixedu-scala-sdk" % "0.1.0"
```
We use [semantic versioning](http://semver.org).

## Usage
### Creating the HTTP Client
In a **production** environment we recommend using [IOApp](https://typelevel.org/cats-effect/datatypes/ioapp.html)
 (or [TaskApp](https://monix.io/api/3.0/monix/eval/TaskApp.html), [zio.App](https://zio.dev/docs/getting_started.html#main)):

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.syntax.all._
import org.fenixedu.sdk.FenixEduClient

object Example extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource.use { implicit httpClient =>
      val client: FenixEduClient[IO] = new FenixEduClient[IO](uri"https://fenix.tecnico.ulisboa.pt/api/fenix/")
      // your code, which eventually must return ExitCode.Success inside an IO
    }
  }
}
``` 

In a **testing** environment we recommend:

```scala
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import cats.effect.{Blocker, ContextShift, IO}
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.syntax.all._
import org.fenixedu.sdk.FenixEduClient
import org.fenixedu.sdk.services._

val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
implicit val cs: ContextShift[IO] = IO.contextShift(ec)
implicit val httpClient: Client[IO] = JavaNetClientBuilder[IO](Blocker.liftExecutionContext(ec)).create
val client: FenixEduClient[IO] = new FenixEduClient[IO](uri"https://fenix.tecnico.ulisboa.pt/api/fenix/")
``` 

[Try it in Scastie](https://scastie.scala-lang.org/cDgS42JbTsGppvKZlbf4mQ)

### Examples
#### Degrees
```scala
// List degrees for the current academic term
client.degrees.list() // returns IO[List[Degree]]
// List degrees for the academic term 2021/2022
client.degrees.list(Some("2021/2022"))

// Get a specific degree
client.degrees.get("2761663971474") // returns IO[Degree]
```

#### Courses
```scala
// List the courses of a degree
val degreeId = "2761663971474" // LEIC-A 2019/2020
client.degrees.courses(degreeId) // returns IO[List[CourseRef]]
// CourseRef is a reference to a course, it does not contain all the information about a Course, but
// contains the course id, acronym, name, and academicTerm. If a FenixEduClient is in the implicit scope
// the course can be easily fetched by invoking `course` of the CourseRef.
 
val courseId = "564560566180817" // Análise e Síntese de Algoritmos 2º Semestre 2019/2020
val course: Course[IO] = client.course(courseId)
 
// Get the course
course.get() // returns IO[Course]

// Get the course evaluations such as exams, tests, projects, etc
course.evaluations // returns IO[List[Evaluation]]

// Get the course groups such teams of students for laboratories or projects
course.groups // returns IO[List[Group]]

// Get the course schedule such as the lesson periods or laboratories shifts.
course.schedule // returns IO[Schedule]

// Lists all the students attending the specified course 
course.students // returns IO[AttendingStudents]
```

#### Parking
```scala
// the number of free parking slots for each parking location.
client.parking // returns IO[Map[String, Parking]]
```

#### Shuttle 
```scala
// the shuttle information, including stops and timetables.
client.shuttle // returns IO[Shuttle]
```

#### Spaces
```scala
// List spaces
client.spaces.list() // returns IO[List[SpaceRef]]
// Just like CourseRef, SpaceRef is a reference to a Space, it does not contain all the information about a Space, but
// contains the space type, id, acronym, and name. If a FenixEduClient is in the implicit scope
// the Space can be easily fetched by invoking `space` of the SpaceRef.

// Get a space
val alameda = "2448131360897"
client.spaces.get(alameda) // returns IO[Space]

// Get the blueprint of a space
// 2448131361063 = Pavilhão Central Floor 0
client.spaces.blueprint("2448131361063") // returns Stream[IO, Byte]
```

## License
This library is open source and available under the [MIT license](LICENSE).
