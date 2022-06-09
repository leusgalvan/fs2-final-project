package server

import fs2._
import cats.effect._

trait Pipes[F[_]] {
  def requests(s: Stream[F, String]): Stream[F, Request]
}

object Pipes {
  def impl[F[_]: RaiseThrowable]: Pipes[F] = new Pipes[F] {
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
            case None                => Pull.output1(request) >> Pull.done
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
                go(currentChunk, restOfStream, step + 1, idx + 1, request)
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
              val leftToRead = request.contentLength - request.body.length
              if (leftToRead <= 0) {
                Pull.output1(request) >> Pull.done
              } else {
                val nextLine = currentChunk(idx)
                val newline = if(leftToRead - nextLine.length > 0) "\n" else ""
                go(
                  currentChunk,
                  restOfStream,
                  step,
                  idx + 1,
                  request.copy(body =
                    request.body ++ nextLine.getBytes ++ newline.getBytes
                  )
                )
              }
          }
        }
      }
      go(Chunk.empty, s, 0, 0, Request.empty).stream
    }
  }
}
