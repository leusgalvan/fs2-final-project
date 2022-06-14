package tcp

import fs2._
import cats.effect._
import cats.implicits._
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * A channel to write and read from a single connection.
 *
 * @tparam F
 */
trait TCPChannel[F[_]] {
  /**
   * The bytes sent by the client through this channel.
   */
  def stream: Stream[F, Byte]

  /**
   * Sends (writes) the given byte array through this channel.
   */
  def write(bytes: Array[Byte]): F[Unit]

  /**
   * Closes the channel.
   */
  def close(): F[Unit]
}

object TCPChannel {
  /**
   * Creates a TCP channel from a given java.nio.SocketChannel.
   *
   * Both reads and writes are done in batches of the specified size.
   */
  def fromSocketChannel[F[_]: Sync](
      socketChannel: SocketChannel,
      bufferSize: Int = 4096
  ): TCPChannel[F] =
    new TCPChannel[F] {
      override def stream: Stream[F, Byte] = {
        /**
         * TODO #3
         *
         * Read one chunk of size 'bufferSize' from 'socketChannel'.
         */
        val readChunk: F[Chunk[Byte]] =
          ???

        Stream.evalUnChunk(readChunk).repeat
      }

      override def write(bytes: Array[Byte]): F[Unit] = {
        bytes.sliding(bufferSize, bufferSize).toList.traverse_ { chunk =>
          Sync[F].blocking {
            val byteBuffer: ByteBuffer = ByteBuffer.allocate(chunk.length)
            byteBuffer.put(chunk)
            byteBuffer.flip()
            socketChannel.write(byteBuffer)
          }
        }
      }

      override def close(): F[Unit] = Sync[F].blocking(socketChannel.close())
    }
}
