import java.net.URL
import java.sql.Date

import Mapper.CustomCol
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf
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
    val toString: UserDefinedFunction = udf[Option[String], String](str => {
      Option(str).map(_.trim)
    })
    val toInt: UserDefinedFunction = udf[Option[Int], String](str => {
      Option(str).flatMap(x => try {
        Some(x.trim.toInt)
      } catch {
        case _: Throwable => None
      })
    })
    val DMYformat = new java.text.SimpleDateFormat("dd-MM-yyyy")
    val toDate: UserDefinedFunction = udf[Option[Date], String](str => {
      Option(str).map(x => new Date(DMYformat.parse(x).getTime))
    })

    val df = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces.customize(Seq(
      CustomCol("name", "first_name", toString),
      CustomCol("age", "total_years", toInt),
      CustomCol("birthday", "d_o_b", toDate)
    ))
    df.show()
    assert(df.count() == 3)
  }

}
