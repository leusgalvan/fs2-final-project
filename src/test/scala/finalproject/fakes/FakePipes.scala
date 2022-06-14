package finalproject.fakes

import cats.Show
import fs2.{Pipe, Stream}
import finalproject.server.{Pipes, Request}

trait FakePipes {
  def oneRequest[F[_]](r: Request): Pipes[F] = multipleRequests(List(r))

  def multipleRequests[F[_]](rs: List[Request]): Pipes[F] = new Pipes[F] {
    private var i = -1
    override def log[A: Show](label: String): Pipe[F, A, A] = x => x
    override def requests: Pipe[F, Byte, Request] = _ => {
      i += 1
      Stream.emit(rs(i))
    }
  }
}
