ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "FS2 Final Project"
  )

addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
)

libraryDependencies += "co.fs2" %% "fs2-core" % "3.2.7"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.2.7"
libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % "test"
libraryDependencies += "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
