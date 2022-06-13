package server

import cats.effect._
import cats.effect.unsafe.IORuntime
import fakes._

class ServerSpec
    extends munit.FunSuite
    with FakeRequestHandlers
    with FakePipes
    with FakeRequests
    with FakeTcpServers
    with FakeTCPChannels {
  implicit val ioRuntime: IORuntime = IORuntime.global
  test(
    "Server writes response bytes to corresponding tcp connection channels"
  ) {
    val fakeTCPChannels: List[ReadWrite[IO]] = List(
      new ReadWrite[IO](getWithBodyStream),
      new ReadWrite[IO](getWithNoBodyStream),
      new ReadWrite[IO](postWithBodyStream)
    )
    val fakeTcpServer = multipleChannels[IO](fakeTCPChannels)
    val fakeRequests = List(
      getWithBodyRequest,
      getWithNoBodyRequest,
      postWithBodyRequest
    )
    val fakeRequestHandler = echoRequestHandler
    val fakePipes: Pipes[IO] = multipleRequests(fakeRequests)
    val server = Server.make[IO](
      maxConnections = 10,
      handleRequest = fakeRequestHandler,
      pipes = fakePipes,
      tcpServer = fakeTcpServer
    )

    server.stream
      .take(fakeTCPChannels.length)
      .compile
      .drain
      .unsafeRunSync()

    fakeTCPChannels.zipWithIndex.foreach { case (tcpChannel, i) =>
      assertEquals(
        tcpChannel.bytes.toList,
        fakeRequestHandler(fakeRequests(i)).bytes.toList
      )
    }
  }
}
