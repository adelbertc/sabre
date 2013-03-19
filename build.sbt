import AssemblyKeys._

name := "sabre"

version := "1.0.1"

organization := "com.adelbertc"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.2"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.10" % "2.1.2"

libraryDependencies += "com.assembla.scala-incubator" % "graph-core_2.10" % "1.6.1"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-core_2.10" % "0.4.2"

libraryDependencies += "com.github.scala-incubator.io" % "scala-io-file_2.10" % "0.4.2"

scalariformSettings

assemblySettings
