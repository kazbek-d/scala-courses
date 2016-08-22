package model

case class Client(val name: String, val cach: BigDecimal, val a: Int, val b: Int, val c: Int, val d: Int){
  override def toString: String = {
    s"$name, $cach, $a, $b, $c, $d"
  }
}

case class Asset(name: String) {
  override def toString: String = name
}

trait Operation
case object Buy extends Operation {
  override def toString: String = "Buy"
}
case object Sale extends Operation{
  override def toString: String = "Sale"
}

case class Order(val clientName: String, val operation: Operation, val asset: Asset, val price: BigDecimal, val quantity: Int) {
  override def toString: String = {
    s"$clientName, $operation, $asset, $price, $quantity"
  }
}
