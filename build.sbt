ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.example"

val fs2Version = "3.2.7"
val munitVersion = "0.7.29"

lazy val root = (project in file("."))
  .settings(
    name := "FS2 Final Project"
  )

addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
)

libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version
libraryDependencies += "co.fs2" %% "fs2-io" % fs2Version
libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
libraryDependencies += "org.scalameta" %% "munit-scalacheck" % munitVersion % Test
