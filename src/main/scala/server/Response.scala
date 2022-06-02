package server

case class Response(
    httpVersion: String,
    status: Int,
    body: Array[Byte]
) {
  def bytes: Array[Byte] =
    s"$httpVersion $status OK".getBytes ++ "\r\n".getBytes ++
      "\r\n".getBytes ++
      body
}
