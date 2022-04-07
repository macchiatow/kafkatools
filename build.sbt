
name := """kafkatools"""
organization := "mt.gamify.kafkatools"
version := "1.1.0"
scalaVersion := "2.12.8"

lazy val skipPublish = Seq(
  publish := {},
  publishLocal := {},
  publishM2 := {}
)

lazy val root = project.in(file("."))
  .settings(
    name := "kafkatools",
    skipPublish
  )
  .aggregate(kafkatools)
  .dependsOn(kafkatools)

lazy val kafkatools = project.in(file("app"))
  .settings(
    skipPublish,
    name := "app",
    mainClass in(Compile, run) := Some("mt.gamify.kafkatools.KafkaToolsApp"),
    packMain := Map("kafkatools" -> "mt.gamify.kafkatools.KafkaToolsApp"),
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "3.1.0",

      // logging
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
    )
  )
  .enablePlugins(PackPlugin)