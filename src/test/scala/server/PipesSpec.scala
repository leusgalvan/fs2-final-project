package server

import cats.implicits._
import cats.effect._
import fakes.FakeRequests
import fs2._

class PipesSpec extends munit.CatsEffectSuite with FakeRequests {
  test("Requests pipe can process a single GET without body") {
    val pipes = Pipes.impl[IO]
    val stream = getWithNoBodyStream.chunkN(1).flatMap(Stream.chunk)
    val expected = getWithNoBodyRequest
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }

  test("Requests pipe can process a single GET with a body") {
    val pipes = Pipes.impl[IO]
    val stream = getWithBodyStream.chunkN(1).flatMap(Stream.chunk)
    val expected = getWithBodyRequest
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }

  test("Requests pipe can process a single POST with a body") {
    val pipes = Pipes.impl[IO]
    val stream = postWithBodyStream.chunkN(1).flatMap(Stream.chunk)
    val expected = postWithBodyRequest
    pipes.requests(stream).compile.toList.flatTap(IO.println).map(_ === List(expected)).assert
  }
}
