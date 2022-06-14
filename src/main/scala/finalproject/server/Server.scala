package finalproject.server

import cats.effect._
import cats.effect.std._
import fs2._
import cats.implicits._
import finalproject.tcp.{TCPChannel, TCPServer}

/**
 * An http server that will handle requests on a given host and port.
 */
trait Server[F[_]] {
  /**
   * An infinite stream that performs the handling of requests.
   *
   * It does not return any value and runs forever (unless some fatal error occurs).
   */
  def stream: Stream[F, Nothing]
}

object Server {
  /**
   * Creates an http server.
   *
   * @param maxConnections the number of concurrent connections we can have at any point in time
   * @param host e.g. 'localhost'
   * @param port e.g. 29000
   * @param handleRequest a function that handles a request and produces a response,
   *                      possibly performing side-effects
   */
  def apply[F[_]](
      maxConnections: Int,
      host: String,
      port: Int,
      handleRequest: Request => F[Response]
  )(implicit
      console: Console[F],
      sync: Sync[F],
      concurrent: Concurrent[F],
      raiseThrowable: RaiseThrowable[F]
  ): Server[F] = {
    val tcpServer = TCPServer.impl[F](host, port)
    val pipes = Pipes.impl[F](console, sync, raiseThrowable)
    make(maxConnections, handleRequest, pipes, tcpServer)
  }

  def make[F[_]: Console: Concurrent](
      maxConnections: Int,
      handleRequest: Request => F[Response],
      pipes: Pipes[F],
      tcpServer: TCPServer[F]
  ): Server[F] =
    new Server[F] {
      def connectionStream(socket: TCPChannel[F]): Stream[F, Nothing] = {
        socket.stream
          .through(pipes.requests)
          .through(pipes.log("[*] New request"))
          .evalMap(handleRequest)
          .through(pipes.log("[*] New response"))
          .evalMap(r => socket.write(r.bytes))
          .handleErrorWith(err => Stream.exec(Console[F].errorln(err.getMessage)))
          .drain
      }

      override def stream: Stream[F, Nothing] = {
        tcpServer.stream
          .map(connectionStream)
          .parJoin(maxConnections)
      }
    }
}
