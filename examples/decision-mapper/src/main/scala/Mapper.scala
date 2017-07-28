import SparkImplicits.spark
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.DataType

object Mapper {
  case class CustomCol(existing_col_name: String, new_col_name: String, cast: String)

  def read(path: String): DataFrame = {
    val df: DataFrame = spark.read.format("CSV").option("header", "true").load(path)
    df.columns.foldLeft(df)((acc, ca) => acc.withColumnRenamed(ca, ca.trim))
  }

  implicit class Hepler(val df: DataFrame) {

    def removeEmptyStringWithSpaces: DataFrame = {
      val cols = df.columns
      df.filter(!_.getValuesMap[String](cols).map(pair=>Option(pair._2).map(_.trim.isEmpty)).filter(_.isDefined).exists(_.get))
    }

    def customize(arguments: Seq[CustomCol]): DataFrame = {
      val cols = df.columns
      arguments
        .foldLeft(df)((acc, ca) => acc.withColumn(ca.new_col_name, df(ca.existing_col_name).cast(ca.cast)))
        .drop(cols: _*)
        //.createOrReplaceTempView("df_csv")
      //spark.sql("SELECT * FROM df_csv")
    }


  }





}
