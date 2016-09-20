
object StrUtils {

  implicit class ExtentionString(val string: String) {
    def increment = string.map(c=>(c + 1).toChar)
    def decrement = string.map(c=>(c - 1).toChar)
  }

}



object Ch_01 extends App {
  import StrUtils._
  println("ABC".increment)
  println("BCD".decrement)

}