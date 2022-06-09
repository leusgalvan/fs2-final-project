package server

import fs2.{Pure, Stream}

trait SampleRequests {
  val getWithNoBodyStream: Stream[Pure, String] = Stream(
    "GET /api/users HTTP/1.1",
    "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
    "Host: www.example.com",
    "Accept-Language: en-us",
    "Accept-Encoding: gzip, deflate",
    "Connection: Keep-Alive",
    ""
  )

  val getWithBodyStream: Stream[Pure, String] = Stream(
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
  )

  val postWithBodyStream: Stream[Pure, String] = Stream(
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
  )

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
