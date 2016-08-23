import com.Matching
import model._
import org.scalatest.FunSuite


object helper {
  def read(path: String) = {
    val stream = getClass.getResourceAsStream(s"/$path.txt")
    scala.io.Source.fromInputStream( stream ).getLines
  }

  def getClients =
    read("clients").map(line => {
      val items = line.split("\t")
      Client(items(0), BigDecimal(items(1)), items(2).toInt, items(3).toInt, items(4).toInt, items(5).toInt)
    }).toList

  def getOrders =
    read("orders").map(line => {
      val items = line.split("\t")
      Order(items(0), if (items(1).equals("s")) Sale else Buy, Asset(items(2)), BigDecimal(items(3)), items(4).toInt)
    }).toStream
}


class MatchingTest extends FunSuite with Matching {

  import helper._

  val clients = getClients
  val orders = getOrders

  test("Generate Result file") {
    setClients(clients)
    val c = getClients

    assert(clients.length === c.length)
  }


}