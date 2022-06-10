package server

import fs2._
import cats._
import cats.implicits._

trait FakeRequests {
  private val crlf = "\r\n".getBytes

  val getWithNoBodyStream: Stream[Pure, Byte] = Stream(
    "GET /api/users HTTP/1.1",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
    "Host: www.example.com",
    "Accept-Language: en-us",
    "Accept-Encoding: gzip, deflate",
    "Connection: Keep-Alive",
    ""
  ).flatMap(l => Stream.emits(l.getBytes ++ crlf))

  val getWithBodyStream: Stream[Pure, Byte] = Stream(
    "GET /api/users HTTP/1.1",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
    "Host: www.example.com",
    "Content-Type: application/json",
    "Content-Length: 15",
    "Accept-Language: en-us",
    "Accept-Encoding: gzip, deflate",
    "Connection: Keep-Alive",
    "",
    "{",
    """  "id": 123""",
    "}"
  ).flatMap(l => Stream.emits(l.getBytes ++ crlf))

  val postWithBodyStream: Stream[Pure, Byte] = Stream(
    "POST /api/users HTTP/1.1",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
    "Host: www.example.com",
    "Content-Type: application/x-www-form-urlencoded",
    "Content-Length: 32",
    "Accept-Language: en-us",
    "Accept-Encoding: gzip, deflate",
    "Connection: Keep-Alive",
    "",
    "username=johndoe&password=123456"
  ).flatMap(l => Stream.emits(l.getBytes ++ crlf))

  val getWithNoBodyRequest: Request = Request(
    method = "GET",
    url = "/api/users",
    httpVersion = "HTTP/1.1",
    headers = Map(
      "User-Agent" -> "Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
      "Host" -> "www.example.com",
      "Accept-Language" -> "en-us",
      "Accept-Encoding" -> "gzip, deflate",
      "Connection" -> "Keep-Alive"
    ),
    body = Array.empty
  )

  val getWithBodyRequest: Request = Request(
    method = "GET",
    url = "/api/users",
    httpVersion = "HTTP/1.1",
    headers = Map(
      "User-Agent" -> "Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
      "Host" -> "www.example.com",
      "Content-Type" -> "application/json",
      "Content-Length" -> "15",
      "Accept-Language" -> "en-us",
      "Accept-Encoding" -> "gzip, deflate",
      "Connection" -> "Keep-Alive"
    ),
    body = """{
             |  "id": 123
             |}""".stripMargin.getBytes
  )

  val postWithBodyRequest: Request = Request(
    method = "POST",
    url = "/api/users",
    httpVersion = "HTTP/1.1",
    headers = Map(
      "User-Agent" -> "Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
      "Host" -> "www.example.com",
      "Content-Type" -> "application/x-www-form-urlencoded",
      "Content-Length" -> "32",
      "Accept-Language" -> "en-us",
      "Accept-Encoding" -> "gzip, deflate",
      "Connection" -> "Keep-Alive"
    ),
    body = "username=johndoe&password=123456".getBytes
  )
}
