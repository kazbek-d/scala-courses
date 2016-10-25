
object Ch02_gettingstarted {

  // Abs value
  /* And
   * value */
  /** Document bloc for ABS value methid  */
  def abs(n: Int): Int =
    if (n < 0) -n else n

  /** Factorial */
  def factorial(n: Int): Int = {
    @annotation.tailrec
    def go(n: Int, acc: Int): Int = {
      if (n <= 0) acc
      else go(n - 1, acc * n)
    }
    go(n, 1)
  }

  /** Fobonachi */
  def fibonachy(n: Int): Int = {
    if (n <= 0) 0
    else if (n == 1) 1
    else {
      @annotation.tailrec
      def go(n: Int, prev: Int, cur: Int): (Int, Int) = {
        if (n <= 1) (prev, cur)
        else go(n - 1, cur, prev + cur)
      }
      val res = go(n, 0, 1)
      res._2
    }
  }

  def absToString(name: String, n: Int, f: Int => Int) = {
    val msg = "The %s value of %d is %d"
    msg.format(name, n, f(n))
  }

  def indexOf[A](as: List[A], p: A => Boolean): Int = {
    @annotation.tailrec
    def loop(n: Int): Int = {
      if (n >= as.length) -1
      else if (p(as(n))) n
      else loop(n + 1)
    }
    loop(0)
  }

  def isSorted[A](as: List[A], ordered: (A, A) => Boolean): Boolean = {
    @annotation.tailrec
    def loop(n: Int): Boolean = {
      if (n >= as.length) true
      else if (ordered(as(n), as(n - 1))) loop(n + 1)
      else false
    }
    loop(1)
  }

  def partial1[A, B, C](a: A, f: (A, B) => C): (B => C) =
    (b: B) => f(a, b)

  def curry[A, B, C](f: (A, B) => C): (A => (B => C)) =
    (a: A) => (b: B) => f(a, b)

  def uncurry[A, B, C](f: A => B => C): ((A, B) => C) =
    (a: A, b: B) => f(a)(b)

  def compose[A, B, C](f: B => C, g: A => B): (A => C) =
    (a: A) => f(g(a))


  def Activate(): Unit = {
    println(absToString("absolute", -43, abs))
    println(absToString("factorial", 7, factorial))
    println(absToString("fibonachy", 5, fibonachy))

    val ints = List(1, 2, 3, 4)
    def isEqual(key: Int)(n: Int) = key == n
    def asc(a: Int, b: Int) = a >= b
    def desc(a: Int, b: Int) = a <= b
    println("Index of 3 in (1, 2, 3, 4) is : " + indexOf(ints, isEqual(3)))
    println("Index of 7 in (1, 2, 3, 4) is: " + indexOf(ints, (x: Int) => 7 == x))
    println("IsSorted ASC in (1, 2, 3, 4) is : " + isSorted(ints, asc))
    println("IsSorted DESC in (1, 2, 3, 4) is : " + isSorted(ints, (x: Int, y: Int) => x <= y))
  }

}
