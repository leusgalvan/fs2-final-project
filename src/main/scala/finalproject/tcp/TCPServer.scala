package finalproject.tcp

import cats.effect._
import fs2._
import java.net._
import java.nio.channels._

/**
 * A server that accepts tcp connections for a given host and port.
 */
trait TCPServer[F[_]] {
  /**
   * A stream of accepted tcp connections.
   *
   * We can handle reads and writes to this connection via its
   * associated TCPChannel.
   */
  def stream: Stream[F, TCPChannel[F]]
}

object TCPServer {
  /**
   * Creates a new tcp server that accepts connection on the given host and port.
   *
   * Each connection is automatically closed after used.
   *
   * Error handling should happen in each connection or else the server will go down.
   *
   * @param hostname e.g. 'localhost'
   * @param port e.g. 29000
   */
  def impl[F[_]: Sync](hostname: String, port: Int): TCPServer[F] = {
    val serverChannelResource: Resource[F, ServerSocketChannel] = {
      Resource.make(
        Sync[F].blocking(
          ServerSocketChannel
            .open()
            .bind(new InetSocketAddress(hostname, port))
        )
      )(s => Sync[F].blocking(s.close()))
    }
    fromServerSocketChannelResource(serverChannelResource)
  }

  /**
   * Method to create a tcp server from a server socket channel without resource semantics.
   * Should only be used in tests.
   */
  def unsafeCreate[F[_]: Sync](channel: ServerSocketChannel): TCPServer[F] = {
    fromServerSocketChannelResource(Resource.pure[F, ServerSocketChannel](channel))
  }

  private def fromServerSocketChannelResource[F[_]: Sync](
      serverSocketChannel: Resource[F, ServerSocketChannel]
  ): TCPServer[F] = new TCPServer[F] {
    def clientChannelResource(
        serverSocketChannel: ServerSocketChannel
    ): Resource[F, TCPChannel[F]] = {
      val acquire = Sync[F].blocking(TCPChannel.fromSocketChannel(serverSocketChannel.accept()))
      Resource.make(acquire)(_.close())
    }

    /**
     * TODO #4
     *
     * Emit the serverSocketChannel as a resource and then use it
     * to repeateadly emit client tcp channels (each channel as a resource).
     */
    override def stream: Stream[F, TCPChannel[F]] =
      ???
  }
}
