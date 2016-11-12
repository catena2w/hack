organization := "hack"

name := "hack"

version := "0.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.+",
  "org.slf4j" % "slf4j-api" % "1.+",
  "com.typesafe.play" %% "play-json" % "2.+",
  "net.databinder.dispatch" %% "dispatch-core" % "0.+"
)
scalacOptions ++= Seq("-Xdisable-assertions")

licenses := Seq("CC0" -> url("https://creativecommons.org/publicdomain/zero/1.0/legalcode"))
