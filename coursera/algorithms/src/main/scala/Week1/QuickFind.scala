package Week1

import scala.io.StdIn


object QuickFind extends App {
  case class Task(isUnion: Boolean, a: Int, b:Int)


  println("Enter array length")
  val arr = Array.fill(StdIn.readLine.toInt){0}
  for(index <- arr.indices) arr(index) = index
  println(s"Array of size = ${arr.length} was created")



  var doMore = true

  def process(task: Task) = {
    val (a,b) = (task.a, task.b)
    println(task)

    if(task.isUnion) {
      println("Initial array:  ", arr.mkString(","))

      val aid = arr(a)
      val bid = arr(b)
      for (index <- arr.indices)
        if (arr(index) == aid) arr(index) = bid

      println("Modified array: ", arr.mkString(","))
    }  else {
      println(s"Is connected: ${arr(a) == arr(b)}")
    }

  }

  while (doMore) {
    println("Enter task: t,a,b (t = u-union, c-connected, a & b = values)")
    val split = StdIn.readLine().split(",")
    split match {
      case Array(t, a, b, _) if (t == "u" || t == "c") && a.forall(_.isDigit) && b.forall(_.isDigit) =>
        process(Task(t == "u", a.toInt, b.toInt))
      case Array(t, a, b) if (t == "u" || t == "c") && a.forall(_.isDigit) && b.forall(_.isDigit) =>
        process(Task(t == "u", a.toInt, b.toInt))
      case _ =>
        doMore = false
        println("Stop the process")
    }
  }

}
