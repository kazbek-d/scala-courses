package model

case class Client(name: String, cash: BigDecimal, a: Int, b: Int, c: Int, d: Int){
  override def toString: String = {
    s"$name\t$cash\t$a\t$b\t$c\t$d"
  }
}

