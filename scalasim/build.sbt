val scala3Version = "3.4.0"

lazy val root = project
  .in(file("."))
  .settings(
      name := "scalasim",
      version := "0.1.0-SNAPSHOT",

      scalaVersion := scala3Version,
      libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,

      // scalatest
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  )

