package server

import cats.implicits._
import fs2._

trait Pipes[F[_]] {
  def requests(s: Stream[F, Byte]): Stream[F, Request]
}

object Pipes {
  def impl[F[_]: RaiseThrowable]: Pipes[F] = new Pipes[F] {
    private val SPACE = ' '.toByte
    private val CR = '\r'.toByte
    private val LF = '\n'.toByte

    private def dropLastCR(chunk: Chunk[Byte]): Chunk[Byte] = {
      if(chunk.last.exists(_ === CR)) {
        chunk.dropRight(1)
      } else chunk
    }

    def requests(s: Stream[F, Byte]): Stream[F, Request] = {
      def go(
          s: Stream[F, Byte],
          step: Int,
          request: Request,
          buffer: Array[Byte]
      ): Pull[F, Request, Unit] = {
        s.pull.uncons.flatMap {
          case Some((chunk, restOfStream)) =>
            step match {
              case 0 => // reading method
                chunk.indexWhere(_ === SPACE) match {
                  case Some(idx) =>
                    val method = new String(buffer ++ chunk.take(idx).toArray)
                    val newReq = request.copy(method = method)
                    go(restOfStream.cons(chunk.drop(idx + 1)), step + 1, newReq, Array.empty)
                  case None =>
                    go(restOfStream, step, request, buffer ++ chunk.toArray)
                }
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
                if(bodyLength <= chunk.size) {
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
