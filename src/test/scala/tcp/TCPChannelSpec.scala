package tcp

import cats.effect._
import fakes.FakeSocketChannel

import java.net.{Socket, SocketAddress, SocketOption}
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util

class TCPChannelSpec extends munit.CatsEffectSuite with FakeSocketChannel {
  test("TCPChannel streams values from a socket") {
    val bytesRead = Array[Byte](1, 2, 3)
    val fakeChannel = new FakeReadableSocketChannel(bytesRead)
    val tcpChannel = TCPChannel.fromSocketChannel[IO](fakeChannel, bufferSize = 2)
    val result = tcpChannel.stream.take(bytesRead.length).compile.toList
    result.assertEquals(bytesRead.toList)
  }

  test("TCPChannel writes values to a socket") {
    val bytesToWrite = Array[Byte](1, 2, 3)
    val fakeChannel = new FakeWritableSocketChannel()
    val tcpChannel = TCPChannel.fromSocketChannel[IO](fakeChannel, bufferSize = 2)
    val result = tcpChannel.write(bytesToWrite) *> IO(fakeChannel.bytes.toList)
    result.assertEquals(bytesToWrite.toList)
  }
}
