package server

trait FakeRequestHandlers {
  val echoRequestHandler: Request => Response = (r: Request) =>
    Response(
      httpVersion = r.httpVersion,
      status = 200,
      body = r.body
    )
}
