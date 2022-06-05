package tcp

import cats.effect._
import tcp.FakeSocketChannel.FakeReadableChannel

class TCPServerSpec extends munit.CatsEffectSuite {
  test("TCPServer emits accepted connections") {
    val socketChannels = List(
      new FakeReadableChannel(Array[Byte](1, 2, 3)),
      new FakeReadableChannel(Array[Byte](4, 5, 6)),
      new FakeReadableChannel(Array[Byte](7, 8, 9, 10))
    )
    val serverSocketChannel = FakeServerSocketChannel.fromSocketChannels(socketChannels)
    val tcpServer = TCPServer.unsafeCreate[IO](serverSocketChannel)
    tcpServer
      .stream
      .take(socketChannels.length)
      .compile
      .toList
      .assertEquals(socketChannels)
  }
}
