package model

case class Client(name: String, cach: BigDecimal, a: Int, b: Int, c: Int, d: Int){
  override def toString: String = {
    s"$name\t$cach\t$a\t$b\t$c\t$d"
  }
}

