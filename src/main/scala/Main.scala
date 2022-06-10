import cats.effect._
import server._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val maxConnections = 1024
    val host = "localhost"
    val port = 29000
    val echoRequestHandler: Request => Response = (r: Request) =>
      Response(
        httpVersion = r.httpVersion,
        status = 200,
        body = r.body,
        headers = Map("Content-Length" -> r.body.length.toString)
      )
    val server = Server[IO](maxConnections, host, port, echoRequestHandler)

    server.stream.compile.drain
  }
}
