package server

import cats.implicits._
import cats.effect._
import fs2._

class PipesSpec extends munit.CatsEffectSuite {
  test("Requests pipe can process a single GET without body") {
    val pipes = Pipes.impl[IO]
    val stream = Stream(
      "GET /api/users HTTP/1.1",
      "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
      "Host: www.example.com",
      "Accept-Language: en-us",
      "Accept-Encoding: gzip, deflate",
      "Connection: Keep-Alive",
      ""
    )
    val expected = Request(
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
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }

  test("Requests pipe can process a single GET with a body") {
    val pipes = Pipes.impl[IO]
    val stream = Stream(
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
    val expected = Request(
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
    pipes
      .requests(stream)
      .compile
      .toList
      .map(_ === List(expected))
      .assert
  }

  test("Requests pipe can process a single POST with a body") {
    val pipes = Pipes.impl[IO]
    val stream = Stream(
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
    val expected = Request(
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
    pipes.requests(stream).compile.toList.map(_ === List(expected)).assert
  }
}
