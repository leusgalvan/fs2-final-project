import cats.effect._
import com.comcast.ip4s._
import fs2._
import fs2.io.net._
import cats.implicits._
import cats._
import server._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val maxConnections = 1024
    val host = host"localhost"
    val port = port"29000"
    val handleRequest = (request: Request) =>
      Response(
        httpVersion = request.httpVersion,
        status = 200,
        body = request.body
      )

    val server = Server(maxConnections, host, port, handleRequest)

    server.stream.compile.drain
  }
}
