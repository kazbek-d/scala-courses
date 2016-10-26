package ch_04

sealed trait Option[+A] {

  def map[B](f: A => B): Option[B] = this match {
    case Some(get) => Some(f(get))
    case _ => None
  }
  def getOrElse[B >: A](default: => B) : B = this match {
    case Some(get) => get
    case _ => default
  }
  def flatMap[B](f: A => Option[B]) : Option[B] = this match {
    case Some(get) => map(f) getOrElse None
    case None => None
  }
  def orElse[B >: A](ob: => Option[B]) : Option[B] = this match {
    case Some(get) => this
    case None => ob
  }
  def filter(f: A => Boolean): Option[A] = this match {
    case Some(get) if f(get) => this
    case _ => None
  }
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

object helper {

  implicit class ExtentionOption(val op: Option[Double]) {
    def lift[A, B](f: A => B): Option[A] => Option[B] = _ map f

    def abs: Option[Double] = lift(math.abs(_: Double))(op)

    def log: Option[Double] = lift(math.log)(op)
  }

}


object ch_04_Handling_errors_without_exceptions extends App {

  case class Employee(name: String, department: String)

  def lookupByName(name: String): Option[Employee] = None

  val joeDepartment: Option[String] = lookupByName("Joe").map(_.department)

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  def variance(xs: Seq[Double]): Option[Double] = {
    @annotation.tailrec
    def loop(seq: Seq[Double], acc: Option[Double]): Option[Double] = seq match {
      case Nil => acc
      case h :: t => loop(t, acc.flatMap(m => Some(math.pow(h - m, 2))))
    }
    loop(xs, Some(0))
  }

  def sequence[A](a: List[Option[A]]): Option[List[A]] = {
    @annotation.tailrec
    def loop(a: List[Option[A]], acc: List[A]): Option[List[A]] = a match {
      case Nil => Some(acc)
      case h :: t => h match {
        case Some(get) => loop(t, acc :+ get)
        case _ => None
      }
    }
    loop(a, List.empty)
  }

  def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a, b) match {
    case (Some(a1), Some(b1)) => Some(f(a1, b1))
    case _ => None
  }

  println("mean of Seq() = " + mean(Seq()))
  println("mean of Seq(0) = " + mean(Seq(0)))
  println("mean of Seq(1, 2, 3) = " + mean(Seq(1, 2, 3)))

  val some1 = Some(1)
  val someMinus7 = Some(-7D)
  val some33 = Some(33)
  val some5 = Some(5)
  val none = None

//  math.abs(1)
//  math.abs(Some(1))

  def plus2(o: Option[Int]) = o.map(_ + 2)

  def lessThat10(o: Option[Int]) = o.filter(_ < 10)

  println("1 plus2 = " + plus2(some1))
  println("none plus2 = " + plus2(none))
  println("33 flatMap = " + some33.flatMap(Some(_)))
  println("5 lessThat10 = " + lessThat10(some5))
  println("none lessThat10 = " + lessThat10(none))
  println("none orElse 10 = " + none.orElse(Some(10)))
  println("none getOrElse 12 = " + none.getOrElse(12))

  println("variance of Seq() = " + variance(Seq()))
  println("variance of Seq(0) = " + variance(Seq(0)))
  println("variance of Seq(1, 2, 3) = " + variance(Seq(1, 2, 3)))

  import helper.ExtentionOption
  println("abs someMinus7 = " + someMinus7.abs)
  println("abs none = " + none.abs)

  println("log some1 = " + someMinus7.log)
  println("abs none = " + none.log)

  println("sequence List(Some(1), Some(2), Some(3)) = " + sequence(List(Some(1), Some(2), Some(3))))
  println("sequence List(Some(1), None, Some(3)) = " + sequence(List(Some(1), None, Some(3))))


}
