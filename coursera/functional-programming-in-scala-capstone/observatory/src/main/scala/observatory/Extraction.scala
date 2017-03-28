package observatory

import java.nio.file.Paths
import java.time.LocalDate

import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

import SparkImplicits.spark
// For implicit conversions like converting RDDs to DataFrames
import spark.implicits._


/**
  * 1st milestone: data extraction
  */
object Extraction { // extends App

  val keysNames = Seq("STN", "WBAN")

  val latLonNames = Seq("Latitude", "Longitude")
  val stationsSchema = StructType(
    StructField(keysNames.head, DataTypes.StringType, nullable = false) ::
      StructField(keysNames(1), DataTypes.StringType, nullable = true) ::
      StructField(latLonNames.head, DataTypes.DoubleType, nullable = true) ::
      StructField(latLonNames(1), DataTypes.DoubleType, nullable = true) ::
      Nil)

  val mdtNames = Seq("Month", "Day", "Temperature")
  val temperatureSchema = StructType(
    StructField(keysNames.head, DataTypes.StringType, nullable = false) ::
      StructField(keysNames(1), DataTypes.StringType, nullable = true) ::
      StructField(mdtNames.head, DataTypes.IntegerType, nullable = false) ::
      StructField(mdtNames(1), DataTypes.IntegerType, nullable = false) ::
      StructField(mdtNames(2), DataTypes.DoubleType, nullable = false) ::
      Nil)


  def read(resource: String): DataFrame = {
    val isStations = resource.contains("stations")

    val rdd = spark.sparkContext.textFile(
      Paths.get(getClass.getResource(resource).toURI).toString
    )

    val data = rdd.map(_.split(",").to[List])
      .filter(cols =>
        if (isStations)
          cols.size == 4
        else
          cols.size == 5)
      .map(cols =>
        if (isStations)
          Row(cols.head, cols(1), cols(2).toDouble, cols(3).toDouble)
        else
          Row(cols.head, cols(1), cols(2).toInt, cols(3).toInt, cols(4).toDouble)
      )

    spark.createDataFrame(data, if (isStations) stationsSchema else temperatureSchema)
  }


  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val stationsDf = read(stationsFile)
    val temperaturesDf = read(temperaturesFile)

    val join = stationsDf.join(temperaturesDf, keysNames, "inner")
      .filter(latLonNames.head + " is not null")
      .filter(latLonNames(1) + " is not null")
      .filter(mdtNames(2) + " < 9999")

    join
      .select(mdtNames.head, mdtNames(1), latLonNames.head, latLonNames(1), mdtNames(2))
      .collect()
      .map(row => (
        LocalDate.of(year, row.getInt(0), row.getInt(1)),
        Location(row.getDouble(2), row.getDouble(3)),
        (row.getDouble(4) - 32) * 5 / 9))
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    spark.sparkContext.parallelize(records.toList)
      .map(row => (row._2, (row._3, 1)))
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
      .mapValues(x => x._1 / x._2)
      .collect()
  }

  //locationYearlyAverageRecords(locateTemperatures(2015, "/stations.csv", "/2015.csv")).foreach(println)

}
