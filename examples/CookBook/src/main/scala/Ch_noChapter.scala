

object Ch_noChapter extends App {

  val n = 100
  (2 to n).collect({
    case x if !(2 until x).exists(x % _ == 0) => x
  }).foreach(println)

}
