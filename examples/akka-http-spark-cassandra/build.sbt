
name := "server"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "2.4.5",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.5",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.m3" %% "curly-scala" % "0.5.+",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M3"
)
