package finalproject.tcp

import cats.effect._
import cats.effect.unsafe.IORuntime
import finalproject.fakes.{FakeServerSocketChannel, FakeSocketChannel}

import java.nio.channels.SocketChannel

class TCPServerSpec extends munit.FunSuite with FakeSocketChannel with FakeServerSocketChannel {
  implicit val ioRuntime: IORuntime = IORuntime.global

  test("TCPServer emits accepted connections") {
    val socketChannels: List[SocketChannel] = List(
      new DummySocketChannel, new DummySocketChannel, new DummySocketChannel
    )
    val serverSocketChannel = fromSocketChannels(socketChannels)
    val tcpServer = TCPServer.unsafeCreate[IO](serverSocketChannel)
    val result = tcpServer
      .stream
      .take(socketChannels.length)
      .compile
      .toList
      .map(_.length)
      .unsafeRunSync()
    assertEquals(result, socketChannels.length)
  }
}
