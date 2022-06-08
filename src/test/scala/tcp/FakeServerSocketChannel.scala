package tcp

import java.net.{ServerSocket, Socket, SocketAddress, SocketOption}
import java.nio.ByteBuffer
import java.nio.channels.{ServerSocketChannel, SocketChannel}
import java.util
import scala.concurrent.duration._
import scala.util.Try

object FakeServerSocketChannel {
  def fromSocketChannels(socketChannels: List[SocketChannel]): ServerSocketChannel = new DummyChannel {
    var channels: List[SocketChannel] = socketChannels
    val blockPeriodAfterLast: Long = 150.millis.toMillis

    override def accept(): SocketChannel = {
      channels match {
        case head :: tail =>
          channels = tail
          head
        case Nil =>
          Thread.sleep(blockPeriodAfterLast)
          throw new Exception("No more channels to accept")
      }
    }
  }

  class DummyChannel extends ServerSocketChannel(null) {
    override def bind(local: SocketAddress, backlog: Int): ServerSocketChannel = ???

    override def setOption[T](name: SocketOption[T], value: T): ServerSocketChannel = ???

    override def socket(): ServerSocket = ???

    override def accept(): SocketChannel = ???

    override def getLocalAddress: SocketAddress = ???

    override def getOption[T](name: SocketOption[T]): T = ???

    override def supportedOptions(): util.Set[SocketOption[_]] = ???

    override def implCloseSelectableChannel(): Unit = ???

    override def implConfigureBlocking(block: Boolean): Unit = ???
  }
}
