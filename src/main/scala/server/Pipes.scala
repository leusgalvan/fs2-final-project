package server

import cats.{Functor, Show}
import cats.effect.std.Console
import cats.implicits._
import fs2._

/**
 * Trait containing all the pipes.
 */
trait Pipes[F[_]] {
  /**
   * A pipe that logs every element of the input stream, appending
   * the given label as a suffix.
   */
  def log[A: Show](label: String): Pipe[F, A, A]

  /**
   * A pipe that parses a request from the given stream of bytes.
   */
  def requests: Pipe[F, Byte, Request]
}

object Pipes {
  /**
   * Creates all the pipes for the given effect.
   */
  def impl[F[_]](implicit
      console: Console[F],
      functor: Functor[F],
      raiseThrowable: RaiseThrowable[F]
  ): Pipes[F] = new Pipes[F] {
    def log[A: Show](label: String): Pipe[F, A, A] =
      _.evalTap(a => Console[F].println(s"$label: ${a.show}"))

    def requests: Pipe[F, Byte, Request] = s => {
      val SPACE = ' '.toByte
      val CR = '\r'.toByte
      val LF = '\n'.toByte
      val validMethods = Set("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS")

      def dropLastCR(chunk: Chunk[Byte]): Chunk[Byte] = {
        if (chunk.last.exists(_ === CR))
          chunk.dropRight(1)
        else
          chunk
      }

      def go(
          s: Stream[F, Byte],
          step: Int,
          request: Request,
          buffer: Array[Byte]
      ): Pull[F, Request, Unit] = {
        s.pull.uncons.flatMap {
          case Some((chunk, restOfStream)) =>
            step match {
              /**
               * TODO #5
               *
               * Read bytes off this chunk up to the first SPACE.
               *
               * Create a String with those bytes and validate that it is a valid
               * http method (use the 'validMethods' val defined above).
               *
               * If it is invalid, raise an error using Pull.raiseError; otherwise, move
               * on to the next step with the remaining bytes.
               *
               * If the SPACE is not present in this chunk, append the chunk bytes to the buffer
               * and continue recursively with the rest of the stream.
               *
               * Hint: look at step 1 for inspiration as it should be pretty similar.
               */
              case 0 => // reading method
                ???

              case 1 => // reading url
                chunk.indexWhere(_ === SPACE) match {
                  case Some(idx) =>
                    val url = new String(buffer ++ chunk.take(idx).toArray)
                    val newReq = request.copy(url = url)
                    go(restOfStream.cons(chunk.drop(idx + 1)), step + 1, newReq, Array.empty)
                  case None =>
                    go(restOfStream, step, request, buffer ++ chunk.toArray)
                }
              case 2 => // reading httpVersion
                chunk.indexWhere(_ === LF) match {
                  case Some(idx) =>
                    val httpVersion = new String(buffer ++ chunk.take(idx - 1).toArray)
                    val newReq = request.copy(httpVersion = httpVersion)
                    go(restOfStream.cons(chunk.drop(idx + 1)), step + 1, newReq, Array.empty)
                  case None =>
                    go(restOfStream, step, request, buffer ++ dropLastCR(chunk).toArray)
                }
              case 3 => // reading header
                chunk.indexWhere(_ === LF) match {
                  case Some(idx) if idx > 1 || buffer.nonEmpty =>
                    val headerStr = new String(buffer ++ chunk.take(idx - 1).toArray)
                    val elems = headerStr.split(": ")
                    val newReq = request.copy(headers = request.headers + (elems(0) -> elems(1)))
                    go(restOfStream.cons(chunk.drop(idx + 1)), step, newReq, Array.empty)
                  case Some(idx) if buffer.isEmpty =>
                    go(restOfStream.cons(chunk.drop(idx + 1)), step + 1, request, Array.empty)
                  case None =>
                    go(restOfStream, step, request, buffer ++ dropLastCR(chunk).toArray)
                }
              case 4 => // Reading body
                val bodyLength = request.contentLength - buffer.length
                if (bodyLength <= chunk.size) {
                  val body = chunk.take(bodyLength)
                  val newReq = request.copy(body = buffer ++ body.toArray)
                  Pull.output1(newReq) >> Pull.done
                } else {
                  go(restOfStream, step, request, buffer ++ chunk.toArray)
                }
            }
          case None => Pull.output1(request) >> Pull.done
        }
      }
      go(s, 0, Request.empty, Array.empty).stream
    }
  }
}
