package finalproject.server

class RequestSpec extends munit.FunSuite {
  test("Request should calculate correct content length when it has the header") {
    val request = Request(
      method = "POST",
      url = "/api",
      httpVersion = "HTTP/1.1",
      headers = Map("Content-Length" -> "15"),
      body = Array.empty
    )
    assertEquals(request.contentLength, 15)
  }

  test("Request should calculate content length 0 when it has no header") {
    val request = Request(
      method = "POST",
      url = "/api",
      httpVersion = "HTTP/1.1",
      headers = Map(),
      body = Array.empty
    )
    assertEquals(request.contentLength, 0)
  }
}
