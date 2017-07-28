import java.net.URL

import Mapper.CustomCol
import org.apache.spark.sql.types._
import org.scalatest.FunSuite

class MapperTest extends FunSuite {

  val sampleCSV: URL = getClass.getResource("sample.csv")

  test("Read a csv file from local filesystem") {
    val df = Mapper.read(sampleCSV.toString)
    df.show()
    assert(df.count() == 6)
  }

  import Mapper.Hepler
  test("Remove rows where any string column is a empty string or just spaces (“”) \nNote : empty string is not same as null") {
    val df = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces
    df.show()
    assert(df.count() == 3)
  }

  test("Customize DataFrame") {
    val df = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces.customize(Seq(
      CustomCol("name", "first_name", "string"),
      CustomCol("age", "total_years", "integer"),
      CustomCol("birthday", "d_o_b", "timestamp")
    ))
    df.show()
    assert(df.count() == 3)
  }

}
