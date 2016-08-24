package model

trait Operation

case object Buy extends Operation {
  override def toString: String = "Buy"
}

case object Sale extends Operation {
  override def toString: String = "Sale"
}

case class SetMatchingOrders(xs: List[MatchingOrders])

case class MatchingOrders(buy: Order, sale: Order) {
  override def toString: String = s"Buy: $buy, Sale: $sale"
}

case class Asset(name: String) {
  override def toString: String = name
}

case class Order(clientName: String, operation: Operation, asset: Asset, price: BigDecimal, quantity: Int) {
  override def toString: String = {
    s"$clientName, $operation, $asset, $price, $quantity"
  }

  def orderPrice = price * quantity
}
