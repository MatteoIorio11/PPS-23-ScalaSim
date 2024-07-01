val scala3Version = "3.4.0"

lazy val root = project
  .in(file("."))
  .settings(
      name := "scalasim",
      version := "0.1.0-SNAPSHOT",

      scalaVersion := scala3Version,
      libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,

      libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",


          // jcodec
      libraryDependencies += "org.jcodec" % "jcodec" % "0.2.5",
      libraryDependencies += "org.jcodec" % "jcodec-javase" % "0.2.5",

      // scalatest
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  )

