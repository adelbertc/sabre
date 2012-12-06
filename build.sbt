import AssemblyKeys._

name := "sabre"

version := "1.0.0-RC1"

organization := "adelbertc"

scalaVersion := "2.10.0-RC3"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10.0-RC3" % "2.1.0-RC3"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.10.0-RC3" % "2.1.0-RC3"

libraryDependencies += "com.assembla.scala-incubator" % "graph-core_2.10.0-RC3" % "1.5.2"

scalariformSettings

assemblySettings