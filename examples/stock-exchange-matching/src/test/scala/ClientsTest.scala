import com.Clients
import model.{Asset, MatchingOrders, _}
import org.scalatest.FunSuite

class ClientsTest extends FunSuite with Clients {

  val c1 = "C1"
  val c2 = "C2"
  val a = Asset("A")
  val b = Asset("B")
  val c = Asset("C")
  val d = Asset("D")

  val clients = List(
    Client(c1, 1000, 2, 4, 8, 16),
    Client(c2, 1000, 16, 8, 4, 2)
  )
  val clientsResult = List(
    Client(c1, 1010, 3, 2, 10, 15),
    Client(c2, 990, 15, 10, 2, 3)
  )

  val matchingOrders = List(
    MatchingOrders(Order(c1, Buy, a, 10, 1), Order(c2, Sale, a, 10, 1)),
    MatchingOrders(Order(c2, Buy, b, 20, 2), Order(c1, Sale, b, 20, 2)),
    MatchingOrders(Order(c1, Buy, c, 30, 2), Order(c2, Sale, c, 30, 2)),
    MatchingOrders(Order(c2, Buy, d, 40, 1), Order(c1, Sale, d, 40, 1))
  )

  test("Get and Set Clients") {
    setClients(clients)
    val c = getClients
    assert(c.length === clients.length)
  }

  test("Update Clients State") {
    setClients(clients)
    updateClientsState(matchingOrders)
    val c = getClients
    c.indices foreach { index =>
      assert(c(index) === clientsResult(index))
    }
  }

}
