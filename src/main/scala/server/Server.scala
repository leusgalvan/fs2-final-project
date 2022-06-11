package server

import cats.Functor
import cats.effect._
import cats.effect.std._
import fs2._
import fs2.io.net._
import cats.implicits._
import tcp.{TCPChannel, TCPServer}

import java.nio.channels.SocketChannel

trait Server[F[_]] {
  def stream: Stream[F, Nothing]
}

object Server {
  def apply[F[_]: RaiseThrowable](
      maxConnections: Int,
      host: String,
      port: Int,
      handleRequest: Request => Response
  )(implicit
      logger: Logger[F],
      console: Console[F],
      concurrent: Concurrent[F],
      sync: Sync[F]
  ): Server[F] = {
    val tcpServer = TCPServer.impl[F](host, port)
    val pipes = Pipes.impl[F]
    make(maxConnections, handleRequest, pipes, tcpServer)
  }

  def make[F[_]: RaiseThrowable](
      maxConnections: Int,
      handleRequest: Request => Response,
      pipes: Pipes[F],
      tcpServer: TCPServer[F]
  )(implicit
      logger: Logger[F],
      console: Console[F],
      concurrent: Concurrent[F],
      sync: Sync[F]
  ): Server[F] =
    new Server[F] {
      def connectionStream(socket: TCPChannel[F]): Stream[F, Nothing] = {
        socket.stream
          .through(pipes.requests)
          .map(handleRequest)
          .evalMap(r => socket.write(r.bytes))
          .drain
      }

      override def stream: Stream[F, Nothing] = {
        tcpServer.stream
          .map(connectionStream)
          .parJoin(maxConnections)
      }
    }
}
