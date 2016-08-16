import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn


object WebServer extends App {

  println("Server Start 127.0.0.1:8081")

  val route = {
    import routs._
    transfer.transferRoute
  }

  import common.implicits._

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println("press any key for exit")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  println("Server End")

}
