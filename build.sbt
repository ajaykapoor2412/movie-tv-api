
name := "sq_panda"

version := "0.1"

scalaVersion := "2.11.12"

lazy val akkaHttpVersion = "10.0.10"
lazy val akkaVersion = "2.5.23"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % "2.5.23"
)