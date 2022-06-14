package finalproject

import cats.effect.{IO, IOApp}
import finalproject.server.{Request, Response, Server}

/** Test application that starts a server at port 29000 of localhost with a simple
  * request handler that just copies the body to the response.
  */
object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val maxConnections = 1024
    val host = "localhost"
    val port = 29000

    /** TODO #7
      *
      * Write a request handler that produces a Response with the following:
      * - The same httpVersion as the request
      * - An Ok status
      * - The same body as the request
      * - A header 'Content-Length' with the number of bytes in the body.
      */
    val echoRequestHandler: Request => IO[Response] =
      ???

    val server = Server[IO](maxConnections, host, port, echoRequestHandler)

    server.stream.compile.drain
  }
}
