package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = Signal[Double] {
    // Δ = b² - 4ac
    val arg: Double = 4.0
    val aVal = a()
    val bVal = b()
    val cVal = c()

    bVal * bVal - arg * aVal * cVal
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = Signal[Set[Double]]{
    // (-b ± √Δ) / 2a
    val arg: Double = 2.0
    val sq: Double = Math.sqrt(delta())
    val bVal = b()
    val aVal = a()

    Set[Double](
      (-bVal + sq) / (arg * aVal),
      (-bVal - sq) / (arg * aVal)
    )
  }

}
