package server

import cats.implicits._
import cats.effect._
import fs2._

class PipesSpec extends munit.CatsEffectSuite with SampleRequests {
  test("Requests pipe can process a single GET without body") {
    val pipes = Pipes.impl[IO]
    val stream = getWithNoBodyStream
    val expected = getWithNoBodyRequest
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }

  test("Requests pipe can process a single GET with a body") {
    val pipes = Pipes.impl[IO]
    val stream = getWithBodyStream
    val expected = getWithBodyRequest
    pipes
      .requests(stream)
      .compile
      .toList
      .map(_ === List(expected))
      .assert
  }

  test("Requests pipe can process a single POST with a body") {
    val pipes = Pipes.impl[IO]
    val stream = postWithBodyStream
    val expected = postWithBodyRequest
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }
}
