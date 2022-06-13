package server

import cats.implicits._
import cats.effect._
import cats.effect.unsafe.IORuntime
import fakes.FakeRequests
import fs2._
import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import org.scalacheck._

class PipesSpec extends ScalaCheckSuite with FakeRequests {
  implicit val ioRuntime: IORuntime = IORuntime.global
  private val pipes = Pipes.impl[IO]

  private def assertHeadersEquals(r: Request, expectedRequest: Request)(implicit loc: munit.Location): Unit = {
    assertEquals(r.headers.size, expectedRequest.headers.size)
    r.headers.toList
      .sortBy(_._1)
      .lazyZip(expectedRequest.headers.toList.sortBy(_._1))
      .foreach { case ((k1, v1), (k2, v2)) =>
        assertStringEquals(k1, k2)
        assertStringEquals(v1, v2)
      }
  }

  private def assertStringEquals(s1: String, s2: String)(implicit loc: munit.Location): Unit = {
    assertEquals(s1.getBytes.toList, s2.getBytes.toList)
  }

  def check(
      requestBytes: Stream[Pure, Byte],
      expectedRequest: Request,
      chunkSize: Int
  )(implicit loc: munit.Location): Unit = {
    val chunkedBytes = requestBytes.chunkN(chunkSize).flatMap(Stream.chunk)

    val result = pipes.requests(chunkedBytes).compile.toList.unsafeRunSync()
    assertEquals(result.length, 1)

    val r = result.head
    assertStringEquals(r.method, expectedRequest.method)
    assertStringEquals(r.url, expectedRequest.url)
    assertStringEquals(r.httpVersion, expectedRequest.httpVersion)
    assertHeadersEquals(r, expectedRequest)
    assertEquals(r.body.toList, expectedRequest.body.toList)
  }

  def chunkSizeGen: Gen[Int] = Gen.chooseNum(1, 4096)

  property("Requests pipe can process a single GET without body") {
    forAllNoShrink(chunkSizeGen) { chunkSize: Int =>
      check(getWithNoBodyStream, getWithNoBodyRequest, chunkSize)
    }
  }

  property("Requests pipe can process a single GET with a body") {
    forAllNoShrink(chunkSizeGen) { (chunkSize: Int) =>
      check(getWithBodyStream, getWithBodyRequest, chunkSize)
    }
  }

  property("Requests pipe can process a single POST with a body") {
    forAllNoShrink(chunkSizeGen) { (chunkSize: Int) =>
      check(postWithBodyStream, postWithBodyRequest, chunkSize)
    }
  }
}
