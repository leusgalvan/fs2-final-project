import cats.effect._
import server.Response.Ok
import server._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val maxConnections = 1024
    val host = "localhost"
    val port = 29000
    val echoRequestHandler: Request => IO[Response] = (r: Request) =>
      IO(
        Response(
          httpVersion = r.httpVersion,
          status = Ok,
          body = r.body,
          headers = Map("Content-Length" -> r.body.length.toString)
        )
      )
    val server = Server[IO](maxConnections, host, port, echoRequestHandler)

    server.stream.compile.drain
  }
}
