package finalproject.fakes

import fs2._
import finalproject.server._

trait FakeRequests {
  val getWithNoBodyStream: Stream[Pure, Byte] = Stream(
    "GET /api/users HTTP/1.1\r\n",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n",
    "Host: www.example.com\r\n",
    "Accept-Language: en-us\r\n",
    "Accept-Encoding: gzip, deflate\r\n",
    "Connection: Keep-Alive\r\n",
    "\r\n"
  ).flatMap(l => Stream.emits(l.getBytes))

  val getWithBodyStream: Stream[Pure, Byte] = Stream(
    "GET /api/users HTTP/1.1\r\n",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n",
    "Host: www.example.com\r\n",
    "Content-Type: application/json\r\n",
    "Content-Length: 15\r\n",
    "Accept-Language: en-us\r\n",
    "Accept-Encoding: gzip, deflate\r\n",
    "Connection: Keep-Alive\r\n",
    "\r\n",
    "{\n",
    "  \"id\": 123\n",
    "}"
  ).flatMap(l => Stream.emits(l.getBytes))

  val postWithBodyStream: Stream[Pure, Byte] = Stream(
    "POST /api/users HTTP/1.1\r\n",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n",
    "Host: www.example.com\r\n",
    "Content-Type: application/x-www-form-urlencoded\r\n",
    "Content-Length: 32\r\n",
    "Accept-Language: en-us\r\n",
    "Accept-Encoding: gzip, deflate\r\n",
    "Connection: Keep-Alive\r\n",
    "\r\n",
    "username=johndoe&password=123456"
  ).flatMap(l => Stream.emits(l.getBytes))

  val invalidMethodStream: Stream[Pure, Byte] = Stream(
    "REMOVE /api/users HTTP/1.1\r\n",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n",
    "Host: www.example.com\r\n",
    "Accept-Language: en-us\r\n",
    "Accept-Encoding: gzip, deflate\r\n",
    "Connection: Keep-Alive\r\n",
    "\r\n"
  ).flatMap(l => Stream.emits(l.getBytes))

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