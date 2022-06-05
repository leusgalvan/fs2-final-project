package tcp

import fs2._
import cats.effect._
import cats._
import cats.implicits._
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

trait TCPChannel[F[_]] {
  def stream: Stream[F, Byte]
  def write(bytes: Array[Byte]): F[Unit]
}

object TCPChannel {
  def fromSocketChannel[F[_]: Sync](
      socketChannel: SocketChannel,
      bufferSize: Int = 4096
  ): TCPChannel[F] =
    new TCPChannel[F] {
      override def stream: Stream[F, Byte] = {
        val readChunk: F[Chunk[Byte]] =
          Sync[F].blocking {
            val byteBuffer: ByteBuffer = ByteBuffer.allocate(bufferSize)
            socketChannel.read(byteBuffer)
            Chunk.array(byteBuffer.array())
          }

        Stream.evalUnChunk(readChunk).repeat
      }

      override def write(bytes: Array[Byte]): F[Unit] = {
        bytes.sliding(bufferSize, bufferSize).toList.traverse_ { chunk =>
          Sync[F].blocking {
            val byteBuffer: ByteBuffer = ByteBuffer.allocate(chunk.length)
            byteBuffer.put(chunk)
            socketChannel.write(byteBuffer)
          }
        }
      }
    }
}
