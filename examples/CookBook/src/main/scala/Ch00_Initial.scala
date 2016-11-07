import scala.util.{Failure, Success, Try}

case class Person(firtName: String, secondName: String)

object Human {
  def unapply(p: Person): Boolean = {
    if (p.firtName.length < 3) false
    else true
  }
}

object Robot {
  def unapplySeq(p: Person): Option[(String, Seq[String])] = {
    val items = p.secondName.split(" ").toSeq
    if (items.length > 2)
      Some(p.firtName + "." + items.head, items.tail)
    else None
  }
}


object Ch00_Initial extends App {

  println("------ unapply ------")

  def greet(p: Person) = p match {
    case p@Human() => println("Mr. " + p.firtName)
    case Robot(partNumber, index, _*) => println(s"Part Number: $partNumber Index: $index")
    case _ => println("NA")
  }

  val list = List(Person("John", "Doe"), Person("2", "34 56 678"))
  list.foreach(greet)

  println("------ and again unapply ------")
  val (person :: robot :: _) = list
  greet(person)
  greet(robot)

  //val a = Some(1)
  //val b = None

  println("------ Option ------")

  def forOpt(a: Option[Int], b: Option[Int]) = {
    for (aa <- a; bb <- b) yield {
      println(aa + bb)
    }
  }

  def addOpt(op: Option[Int]): Option[Int] = op.map(_ + 10)

  forOpt(Some(1), None)
  println(addOpt(Some(5)))
  println(addOpt(None))


  println("------ Try ------")

  def devTry(a: Int, b: Int): Try[Int] = Try {
    a / b
  }

  println(devTry(10, 5))
  println(devTry(10, 0).recover({ case _ => 0 }))
  println(devTry(10, 0) match {
    case Failure(exception) => exception
    case Success(value) => value
  })

  println("------ Either ------")

  def tostrEither(a: Int): Either[String, String] = if (a < 0) Left(math.abs(a).toString) else Right(a.toString)

  List(-1,2).map(i=>
  tostrEither(i) match {
    case Left(a) => s"Negative $a"
    case Right(b) => s"Positive $b"
  }).foreach(println)

  val rightRight: List[Either[String, Either[String, Int]]] = List(Left("Negative 2"), Right(Right(3)))

  rightRight.collect { case Right(a) if a.isRight => a.fold(x => x, x => x.toString) } foreach println


}
