package finalproject.fakes

import cats.{Applicative, ApplicativeError}
import finalproject.server.Response.Ok
import finalproject.server._

trait FakeRequestHandlers {
  def echoRequestHandler[F[_]](implicit F: Applicative[F]): Request => F[Response] = (r: Request) =>
    F.pure(
      Response(
        httpVersion = r.httpVersion,
        status = Ok,
        body = r.body,
        headers = Map("Connection" -> "Closed")
      )
    )

  def failOnPostRequestHandler[F[_]](implicit F: ApplicativeError[F, Throwable]): Request => F[Response] =
    (r: Request) =>
      if (r.method == "POST")
        F.raiseError(new Exception("Failed handling request"))
      else {
        F.pure(
          Response(
            httpVersion = r.httpVersion,
            status = Ok,
            body = r.body,
            headers = Map("Connection" -> "Closed")
          )
        )
      }
}
