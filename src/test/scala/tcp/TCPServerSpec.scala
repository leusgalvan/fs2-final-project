package tcp

import cats.effect._
import fakes.{FakeServerSocketChannel, FakeSocketChannel}

import java.nio.channels.SocketChannel
import scala.concurrent.duration._

class TCPServerSpec extends munit.CatsEffectSuite with FakeSocketChannel with FakeServerSocketChannel {
  test("TCPServer emits accepted connections") {
    val socketChannels: List[SocketChannel] = List(
      new DummySocketChannel, new DummySocketChannel, new DummySocketChannel
    )
    val serverSocketChannel = fromSocketChannels(socketChannels)
    val tcpServer = TCPServer.unsafeCreate[IO](serverSocketChannel)
    tcpServer
      .stream
      .take(socketChannels.length)
      .compile
      .toList
      .map(_.length)
      .assertEquals(socketChannels.length)
  }
}
