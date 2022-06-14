package finalproject.fakes

import finalproject.tcp._
import cats.effect._
import fs2._

trait FakeTCPChannels {
  class ReadWrite[F[_]: Sync](readBytes: Stream[F, Byte])
      extends TCPChannel[F] {
    var bytes: Array[Byte] = Array.empty

    override def stream: Stream[F, Byte] = readBytes

    override def write(bs: Array[Byte]): F[Unit] =
      Sync[F].delay {
        bytes = bytes ++ bs
      }

    override def close(): F[Unit] = Sync[F].unit
  }
}
