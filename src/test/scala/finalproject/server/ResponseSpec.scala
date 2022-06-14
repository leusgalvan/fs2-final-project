package finalproject.server

import finalproject.server.Response.Ok

class ResponseSpec extends munit.FunSuite {
  test("Response is correctly encoded to bytes") {
    val response = Response(
      httpVersion = "HTTP/1.1",
      status = Ok,
      body = "{\"id\":123}".getBytes,
      headers = Map(
        "Content-Type" -> "application/json",
        "Content-Length" -> "10"
      )
    )
    val encoded = new String(response.bytes)
    val expected =
      "HTTP/1.1 200 OK\r\n" +
      "Content-Type: application/json\r\n" ++
      "Content-Length: 10\r\n" ++
      "\r\n" ++
      "{\"id\":123}"
    assertEquals(encoded, expected)
  }
}
