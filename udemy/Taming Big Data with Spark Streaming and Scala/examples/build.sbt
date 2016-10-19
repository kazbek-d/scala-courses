name := "examples"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.0" % "provided",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
  "org.apache.spark" % "spark-streaming-flume_2.11" % "2.0.1",
  "org.apache.spark" % "spark-streaming-kinesis-asl_2.11" % "2.0.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M3"
)