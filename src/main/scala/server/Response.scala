package server

import cats._

case class Response(
    httpVersion: String,
    status: Response.Status,
    body: Array[Byte],
    headers: Map[String, String]
) {
  def bytes: Array[Byte] =
    s"$httpVersion ${status.code} ${status.reason}\r\n".getBytes ++
      headers.map { case (k, v) => s"$k: $v\r\n" }.mkString.getBytes ++
      "\r\n".getBytes ++
      body

  override def toString: String = Response.show.show(this)
}

object Response {
  abstract class Status(val code: Int, val reason: String)
  object Ok extends Status(200, "OK")
  object NotFound extends Status(404, "Not Found")
  object InternalServerError extends Status(500, "Internal Server Error")

  implicit val show: Show[Response] = Show.show { response =>
    s"""Response(
       |  httpVersion = ${response.httpVersion}
       |  status = ${response.status.code} ${response.status.reason}
       |  headers = [
       |    ${response.headers.map { case (k, v) => s"$k: $v" }.mkString("\n    ")}
       |  ]
       |  body = ${new String(response.body)}
       |)""".stripMargin
  }
}