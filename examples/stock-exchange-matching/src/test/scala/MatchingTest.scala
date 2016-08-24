import com.Matching
import model._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.Success


object helper {
  def read(name: String) = {
    val stream = getClass.getResourceAsStream(s"/$name.txt")
    scala.io.Source.fromInputStream( stream ).getLines
  }

  def write(name: String, content: String) = {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets

    Files.write(Paths.get(s"$name.txt"), content.getBytes(StandardCharsets.UTF_8))
  }

  def readClientsfromFile =
    read("clients").map(line => {
      val items = line.split("\t")
      Client(items(0), BigDecimal(items(1)), items(2).toInt, items(3).toInt, items(4).toInt, items(5).toInt)
    }).toList

  def readOrdersFromFile =
    read("orders").map(line => {
      val items = line.split("\t")
      Order(items(0), if (items(1).equals("s")) Sale else Buy, Asset(items(2)), BigDecimal(items(3)), items(4).toInt)
    }).toStream
}


class MatchingTest extends FunSuite with Matching {

  import helper._

  val clients = readClientsfromFile
  val orders = readOrdersFromFile

  test("Generate Result file") {

    setClients(clients)

    orders.foreach(addOrder)

    Thread.sleep(1000)

    getClients onComplete {
      case Success(value) => value match {
        case ClientsList(xs) => {
          write("result", xs.sortBy(_.name).mkString("\n"))
          assert(clients.length === xs.length)
        }
        case _ =>
      }
      case _ =>
    }

  }


}