import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import data.Repository
import data.DefaultRepository
import routs.SalesRout

import scala.concurrent.Future
import scala.io.StdIn


class Server(implicit repository: Repository) {

  val sales = new SalesRout()
  val route = {
    sales.salesRoute
  }

  import common.implicits._

  private var bindingFuture: Future[ServerBinding] = null

  def bind(ip:String = "localhost", port : Int = 8081) =
    bindingFuture = Http().bindAndHandle(route, ip, port)

  def unbind =
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}

object WebServer extends App  {

  implicit val repository = new DefaultRepository
  val server = new Server()

  println("Server Start 127.0.0.1:8081")
  server.bind()
  println("press any key for exit")
  StdIn.readLine()
  server.unbind
  println("Server End")

}
