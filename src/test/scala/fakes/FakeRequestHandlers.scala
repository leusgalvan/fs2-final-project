package fakes

import server.Response.Ok
import server._

trait FakeRequestHandlers {
  val echoRequestHandler: Request => Response = (r: Request) =>
    Response(
      httpVersion = r.httpVersion,
      status = Ok,
      body = r.body,
      headers = Map("Connection" -> "Closed")
    )
  val failOnPostRequestHandler: Request => Response = (r: Request) =>
    if (r.method == "POST") throw new Exception("Failed handling request")
    else
      Response(
        httpVersion = r.httpVersion,
        status = Ok,
        body = r.body,
        headers = Map("Connection" -> "Closed")
      )
}
