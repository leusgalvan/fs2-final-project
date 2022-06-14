package finalproject.fakes

import java.nio._
import java.nio.channels._
import java.net._

trait FakeSocketChannel {
  class DummySocketChannel extends SocketChannel(null) {
    override def bind(local: SocketAddress): SocketChannel = ???

    override def setOption[T](name: SocketOption[T], value: T): SocketChannel =
      ???

    override def shutdownInput(): SocketChannel = ???

    override def shutdownOutput(): SocketChannel = ???

    override def socket(): Socket = ???

    override def isConnected: Boolean = ???

    override def isConnectionPending: Boolean = ???

    override def connect(remote: SocketAddress): Boolean = ???

    override def finishConnect(): Boolean = ???

    override def getRemoteAddress: SocketAddress = ???

    override def read(dst: ByteBuffer): Int = ???

    override def read(dsts: Array[ByteBuffer], offset: Int, length: Int): Long =
      ???

    override def write(src: ByteBuffer): Int = ???

    override def write(
        srcs: Array[ByteBuffer],
        offset: Int,
        length: Int
    ): Long = ???

    override def getLocalAddress: SocketAddress = ???

    override def implCloseSelectableChannel(): Unit = ()

    override def implConfigureBlocking(block: Boolean): Unit = ???

    override def getOption[T](name: SocketOption[T]): T = ???

    override def supportedOptions(): java.util.Set[SocketOption[_]] = ???
  }

  class FakeReadableSocketChannel(bytesToRead: Array[Byte]) extends DummySocketChannel {
    var lastIndex = 0

    override def read(dst: ByteBuffer): Int = {
      val n = math.min(dst.capacity(), bytesToRead.length - lastIndex)
      dst.put(bytesToRead, lastIndex, n)
      lastIndex += n
      n
    }
  }

  class FakeWritableSocketChannel() extends DummySocketChannel {
    var bytes: Array[Byte] = Array.empty
    override def write(src: ByteBuffer): Int = {
      val bs = src.array()
      bytes = bytes ++ bs
      bs.length
    }
  }
}
