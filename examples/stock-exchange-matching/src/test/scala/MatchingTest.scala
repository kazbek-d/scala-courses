import model.{Buy, Client, Order, Sale, Asset}
import org.scalatest.FunSuite


object helper {

  def read(path: String) = {
    val stream = getClass.getResourceAsStream(s"/$path.txt")
    scala.io.Source.fromInputStream( stream ).getLines
  }

  def getClients =
    read("clients").map(line => {
      val items = line.split("\t")
      items(0) -> Client(items(0), BigDecimal(items(1)), items(2).toInt, items(3).toInt, items(4).toInt, items(5).toInt)
    }).toMap

  def getOrders =
    read("orders").map(line => {
      val items = line.split("\t")
      Order(items(0), if (items(1).equals("s")) Sale else Buy, Asset(items(2)), BigDecimal(items(3)), items(4).toInt)
    }).toStream

}

class MatchingTest extends FunSuite {

  import helper._

  val clients = getClients
  val orders = getOrders

  test("Test 1") {
    assert(clients.size === 9)
  }

  test("Test 2") {
    assert(111 === 23.15)
  }

}