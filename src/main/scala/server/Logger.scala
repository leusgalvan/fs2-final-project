package server

import cats.Show
import cats.effect.IO
import cats.implicits._
import fs2._

trait Logger[F[_]] {
  def log[A: Show](label: String, value: A): Stream[F, Nothing]

  def logF[A: Show](label: String, valueF: F[A]): Stream[F, Nothing]
}

object Logger {
  def apply[F[_]](implicit ev: Logger[F]): Logger[F] = ev

  implicit val loggerIO: Logger[IO] = new Logger[IO] {
    override def log[A: Show](label: String, value: A): Stream[IO, Nothing] =
      Stream.eval(IO.println(s"$label: ${value.show}")).drain

    override def logF[A: Show](label: String, valueF: IO[A]): Stream[IO, Nothing] = {
      Stream.eval(valueF).flatMap(log(label, _)).drain
    }
  }
}