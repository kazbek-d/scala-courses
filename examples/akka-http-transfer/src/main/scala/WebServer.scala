import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.Future
import scala.io.StdIn


trait Server {

  val route = {
    import routs._
    transfer.transferRoute
  }

  import common.implicits._

  private var bindingFuture: Future[ServerBinding] = null

  def bind(ip:String = "localhost", port : Int = 8081) =
    bindingFuture = Http().bindAndHandle(route, ip, port)

  def unbind =
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

}

object WebServer extends App with Server {

  println("Server Start 127.0.0.1:8081")
  bind()
  println("press any key for exit")
  StdIn.readLine()
  unbind
  println("Server End")

}
