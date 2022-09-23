name := "demo"
version := "1.0"
scalaVersion := "2.12.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"
libraryDependencies += "com.github.tomakehurst" % "wiremock" % "2.27.2" % "test"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.5"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.0"
