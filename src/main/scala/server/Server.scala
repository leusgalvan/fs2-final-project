package server

import cats.effect._
import cats.effect.std._
import com.comcast.ip4s._
import fs2._
import fs2.io.net._
import cats.implicits._

trait Server[F[_]] {
  def stream: Stream[F, Nothing]
}

object Server {
  def apply[F[_]](
      maxConnections: Int,
      host: Host,
      port: Port,
      handleRequest: Request => Response
  )(implicit
      logger: Logger[F],
      console: Console[F],
      monadCancel: MonadCancel[F, Throwable],
      network: Network[F],
      concurrent: Concurrent[F]
  ): Server[F] =
    new Server[F] {
      def requests(s: Stream[F, String]): Stream[F, Request] = {
        def go(
            currentChunk: Chunk[String],
            restOfStream: Stream[F, String],
            step: Int,
            idx: Int,
            request: Request
        ): Pull[F, Request, Unit] = {
          if (idx >= currentChunk.size) {
            restOfStream.pull.uncons.flatMap {
              case Some((newChunk, s)) => go(newChunk, s, step, 0, request)
              case None                => Pull.done
            }
          } else {
            step match {
              case 0 => // reading first line
                val elems = currentChunk(0).split(" ")
                if (elems.size < 3)
                  Pull.raiseError[F](
                    new Exception(
                      "Request line should contain METHOD, URL and HTTP_VERSION"
                    )
                  )
                else
                  go(
                    currentChunk,
                    restOfStream,
                    step + 1,
                    idx + 1,
                    request.copy(
                      method = elems(0),
                      url = elems(1),
                      httpVersion = elems(2)
                    )
                  )

              case 1 => // reading headers
                val nextLine = currentChunk(idx)
                if (nextLine.isEmpty)
                  go(currentChunk, restOfStream, step + 1, idx, request)
                else {
                  val elems = nextLine.split(": ")
                  if (elems.size < 2)
                    Pull.raiseError[F](
                      new Exception(
                        "A header should have the format 'KEY: VALUE'"
                      )
                    )
                  else
                    go(
                      currentChunk,
                      restOfStream,
                      step,
                      idx + 1,
                      request.copy(headers =
                        request.headers + (elems(0) -> elems(1))
                      )
                    )
                }

              case 2 => // reading body
                val nextLine = currentChunk(idx)
                if (nextLine.isEmpty)
                  Pull.output1(
                    request
                  ) >> Pull.done //go(currentChunk, restOfStream, 0, idx + 1, Request.empty)
                else
                  go(
                    currentChunk,
                    restOfStream,
                    step,
                    idx + 1,
                    request.copy(body = request.body ++ nextLine.getBytes)
                  )
            }
          }
        }
        go(Chunk.empty, s, 0, 0, Request.empty).stream
      }

      def connectionStream(socket: Socket[F]): Stream[F, Nothing] = {
        logger.logF("Handling new connection", socket.remoteAddress) ++
          socket.reads
            .through(text.utf8.decode)
            .through(text.lines)
            .through(requests)
            .evalTap(req => console.println(req.show))
            .map(handleRequest)
            .evalMap(response =>
              socket.write(Chunk.array(response.bytes)) >> socket.endOfOutput
            )
            .drain
      }

      override def stream: Stream[F, Nothing] = {
        Stream
          .resource(network.serverResource(Some(host), Some(port)))
          .flatMap { case (ip, sockets) =>
            logger.log("Server started at ip", ip) ++
              sockets.map(connectionStream).parJoin(maxConnections)
          }
      }
    }
}
