
object Ch_noChapter extends App {

  val n = 10
  private val pf: PartialFunction[Int, Int] = {
    case x if !(2 until x).exists(x % _ == 0) => x
  }
  val lift = pf.lift

  println("PartialFunction")
  (2 to n).collect(pf).foreach(println)
  println("PartialFunction -> lift")
  (2 to n).map(pf.lift).foreach(println)

}





object State {
  import annotation.implicitNotFound
  @implicitNotFound("No member of type class Comparing in scope for ${T}")
  trait Comparing[T] {
    def higher(x:T, y:T) : T
    def heavier(x:T, y:T) : T
  }
}

object PersonState {
  case class Human(name: String, weight: Int, height: Int)

  import State._
  implicit object HumanComparing extends Comparing[Human] {
    def higher(x: Human, y: Human): Human = if(x.height >= y.height) x else y
    def heavier(x: Human, y: Human): Human = if(x.weight >= y.weight) x else y
  }

}

object Statistics extends App {
  import State.Comparing

  def highest[T](xs: List[T])(implicit ev: Comparing[T]) : T = xs.reduce(ev.higher)
  def heaviest[T](xs: List[T])(implicit  ev: Comparing[T]) : T = xs.reduce(ev.heavier)

  import PersonState.Human
  val list = List(Human("Mark", 80, 190), Human("Bob", 90, 185), Human("Piter", 70, 180), Human("Joe", 60, 175))
  println(highest(list))
  println(heaviest(list))

}