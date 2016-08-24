import com.OrdersBook
import model._
import org.scalatest.FunSuite

class OrdersBookTest extends FunSuite with OrdersBook {

  val c1 = "C1"
  val c2 = "C2"
  val c3 = "C3"
  val c4 = "C4"
  val a = Asset("A")

  val res1 = MatchingOrders(Order(c3, Buy, a, 12, 5), Order(c2, Sale, a, 12, 5))

  test("Insert Order") {

    assert(insertOrder(Order(c1, Buy, a, 9, 100)) === empty)
    assert(insertOrder(Order(c2, Sale, a, 11, 100)) === empty)
    assert(insertOrder(Order(c3, Buy, a, 12, 5)).head === res1)

    assert(9 === 9)
  }

}
