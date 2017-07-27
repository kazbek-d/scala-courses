
name := "decision-mapper"

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion =  "2.1.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-catalyst" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion
)

//libraryDependencies ++= Seq(
//  "org.apache.spark" %% "spark-core" % sparkVersion % "provided" exclude("com.google.guava", "guava"),
//  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided" exclude("com.google.guava", "guava"),
//  "org.apache.spark" %% "spark-catalyst" % sparkVersion % "provided" exclude("com.google.guava", "guava"),
//  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided" exclude("com.google.guava", "guava")
//)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case conf :String if conf.contains(".conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}