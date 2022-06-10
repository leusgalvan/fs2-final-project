package server

import fs2._

trait FakePipes {
  def oneRequest[F[_]](r: Request): Pipes[F] = multipleRequests(List(r))

  def multipleRequests[F[_]](rs: List[Request]): Pipes[F] = new Pipes[F] {
    override def requests(s: Stream[F, Byte]): Stream[F, Request] = {
      Stream.emits(rs)
    }
  }
}
