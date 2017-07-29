import java.net.URL
import java.sql.Date

import Mapper.CustomCol
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf
import org.scalatest.FunSuite


class MapperTest extends FunSuite {

  import Mapper.Helper

  val sampleCSV: URL = getClass.getResource("sample.csv")
  val toStr: UserDefinedFunction = udf[Option[String], String](str => {
    Option(str).map(_.trim)
  })
  val toInt: UserDefinedFunction = udf[Option[Int], String](str => {
    Option(str).flatMap(x => try {
      Some(x.trim.toInt)
    } catch {
      case _: Throwable => None
    })
  })
  val toDate: UserDefinedFunction = udf[Option[Date], String](str => {
    Option(str).map(x => new Date(new java.text.SimpleDateFormat("dd-MM-yyyy").parse(x).getTime))
  })
  val customCols = Seq(
    CustomCol("name", "first_name", toStr),
    CustomCol("age", "total_years", toInt),
    CustomCol("birthday", "d_o_b", toDate)
  )



  test("Read a csv file from local filesystem") {
    val df = Mapper.read(sampleCSV.toString)
    df.show()
    assert(df.count() == 6)
  }


  test("Remove rows where any string column is a empty string or just spaces (“”) \nNote : empty string is not same as null") {
    val df = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces
    df.show()
    assert(df.count() == 3)
  }


  test("Customize DataFrame") {
    val df = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces.customize(customCols)
    df.show()
    assert(df.count() == 3)
  }


  test("For a given column, provide profiling information\ni.e. total number of unique values, count of each unique value.\nExclude - nulls") {
    val profile = Mapper.read(sampleCSV.toString).removeEmptyStringWithSpaces.customize(customCols).profiling
    profile.foreach(println)
    assert(profile.length == 3)
  }

}
