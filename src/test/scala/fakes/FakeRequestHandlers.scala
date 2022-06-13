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
}
