import cats.effect._
import server._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val maxConnections = 1024
    val host = "localhost"
    val port = 29000
    val handleRequest = (request: Request) =>
      Response(
        httpVersion = request.httpVersion,
        status = 200,
        body = request.body
      )
    val server = Server[IO](maxConnections, host, port, handleRequest)

    server.stream.compile.drain
  }
}
