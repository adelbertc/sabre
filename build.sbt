import AssemblyKeys._

name := "sabre"

version := "1.0.0-RC6"

organization := "com.adelbertc"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.0"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.10" % "2.1.0"

libraryDependencies += "com.assembla.scala-incubator" % "graph-core_2.10" % "1.6.0"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-core_2.10" % "0.4.1"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-file_2.10" % "0.4.1"

scalariformSettings

assemblySettings
