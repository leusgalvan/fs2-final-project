package server

import cats._
import cats.implicits._

case class Response(
    httpVersion: String,
    status: Int,
    body: Array[Byte],
    headers: Map[String, String]
) {
  def bytes: Array[Byte] =
    s"$httpVersion $status OK".getBytes ++ "\n".getBytes ++
      headers.map { case (k, v) => s"$k: $v\r\n" }.mkString.getBytes ++
      "\r\n".getBytes ++
      body

  override def toString: String = Response.show.show(this)
}

object Response {
  implicit val show: Show[Response] = Show.show { response =>
    s"""Response(
       |  httpVersion = ${response.httpVersion}
       |  status = ${response.status}
       |  headers = [
       |    ${response.headers.map { case (k, v) => s"$k: $v" }.mkString("\n    ")}
       |  ]
       |  body = ${new String(response.body)}
       |)""".stripMargin
  }
}