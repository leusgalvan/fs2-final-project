package server

import cats._
import cats.implicits._

case class Request(
    method: String,
    url: String,
    httpVersion: String,
    headers: Map[String, String],
    body: Array[Byte]
) {
  def contentLength: Int =
    headers.get("Content-Length").flatMap(_.toIntOption).getOrElse(0)

  override def toString: String = Request.show.show(this)
}

object Request {
  val empty = new Request("", "", "", Map.empty, Array.empty)

  implicit val show: Show[Request] = Show.show { req =>
    s"""Request(
       |  method = ${req.method}
       |  url = ${req.url}
       |  httpVersion = ${req.httpVersion}
       |  headers = [
       |    ${req.headers.map { case (k, v) => s"$k: $v" }.mkString("\n    ")}
       |  ]
       |  body = ${new String(req.body)}
       |)""".stripMargin
  }

  implicit val deepEq: Eq[Request] = Eq.instance { (r1, r2) =>
    r1.method === r2.method &&
    r1.url === r2.url &&
    r1.httpVersion === r2.httpVersion &&
    r1.headers === r2.headers
    r1.body.toList === r2.body.toList
  }
}
