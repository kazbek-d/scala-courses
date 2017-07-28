import java.net.URL

import org.scalatest.FunSuite

class BeginnerTest extends FunSuite {

  val sampleCSV: URL = getClass.getResource("sample.csv")


  test("Read SCV file ") {
    val df = Beginner.read(sampleCSV.toString)
    df.show()
    assert(df.count() == 6)
  }



}
