package server

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import tcp.{FakeTCPChannels, FakeTcpServers}
import scala.concurrent.duration._

import fs2._

class ServerSpec extends munit.CatsEffectSuite with FakeRequestHandlers with FakePipes with FakeRequests with FakeTcpServers with FakeTCPChannels {
  test("Server works ok") {
    val fakeTCPChannels: List[ReadWrite[IO]] = List(
      new ReadWrite[IO](getWithBodyStream),
      new ReadWrite[IO](getWithNoBodyStream),
      new ReadWrite[IO](postWithBodyStream)
    )
    val server = Server.make[IO](
      maxConnections = 10,
      handleRequest = echoRequestHandler,
      pipes = Pipes.impl[IO],
      tcpServer = multipleChannels[IO](fakeTCPChannels)
    )
    server
      .stream
      .interruptAfter(100.millis)
      .compile
      .drain
      .flatMap { _ =>
        fakeTCPChannels.traverse_ { tcpChannel =>
          IO.println(new String(tcpChannel.bytes))
        }
      }
  }
}
