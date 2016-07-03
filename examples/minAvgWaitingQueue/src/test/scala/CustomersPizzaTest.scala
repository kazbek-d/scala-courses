package QueuePackage

import org.scalatest.FunSuite


class CustomersPizzaTest extends FunSuite with CookTaskOptimizer {

  val customersOrderList: List[CustomersPizza] = List(
    CustomersPizza(1, 0, 3),
    CustomersPizza(2, 1, 9),
    CustomersPizza(3, 2, 6)
  )

  test("Minimum Average Waiting Time -> minLatency") {
    assert(calculate(customersOrderList).minLatency === 9)
  }

  test("Minimum Average Waiting Time -> customersOrder") {
    assert(calculate(customersOrderList).customersOrder === List(1, 3, 2))
  }

  test("Minimum Average Waiting Time -> minLatency Test 2") {
    assert(calculate(List(
      CustomersPizza(1, 0, 3),
      CustomersPizza(2, 1, 9),
      CustomersPizza(3, 2, 5)
    )).minLatency === 8)
  }

}
