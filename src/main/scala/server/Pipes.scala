package server

import fs2._
import cats.effect._

trait Pipes[F[_]] {
  def requests(s: Stream[F, Byte]): Stream[F, Request]
}

object Pipes {
  def impl[F[_]: RaiseThrowable]: Pipes[F] = new Pipes[F] {
    def requests(s: Stream[F, Byte]): Stream[F, Request] = {
      ???
    }
  }
}
