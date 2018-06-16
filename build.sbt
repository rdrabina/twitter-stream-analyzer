name := "twitter-stream-analyzer"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.1"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.danielasfregola" %% "twitter4s" % "5.5",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.github.wookietreiber" %% "scala-chart" % "latest.integration",
  "org.scala-lang" % "scala-swing" % "2.10+"
)
