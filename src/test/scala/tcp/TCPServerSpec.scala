package tcp

import cats.effect._
import fakes.FakeServerSocketChannel
import fakes.FakeSocketChannel.{DummyChannel, FakeReadableChannel}

import java.nio.channels.SocketChannel
import scala.concurrent.duration._

class TCPServerSpec extends munit.CatsEffectSuite {
  test("TCPServer emits accepted connections") {
    val socketChannels: List[SocketChannel] = List(
      new DummyChannel, new DummyChannel, new DummyChannel
    )
    val serverSocketChannel = FakeServerSocketChannel.fromSocketChannels(socketChannels)
    val tcpServer = TCPServer.unsafeCreate[IO](serverSocketChannel)
    tcpServer
      .stream
      .interruptAfter(100.millis)
      .compile
      .toList
      .map(_.length)
      .assertEquals(socketChannels.length)
  }
}
