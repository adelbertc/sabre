#Sabre (WIP)
Sabre is a distributed (in-memory) graph processing framework
designed to make distribution of "trivially parallelizable"
graph computations across a compute grid nice and easy.

Sabre is written with 
[Scala](http://www.scala-lang.org/) 2.10.0-RC2,
[Akka](http://akka.io/) 2.1.0-RC2,
and [Graph for Scala](https://www.assembla.com/spaces/scala-graph/wiki) 1.5.2.

# Instructions
Sabre is written with the [Simple Build Tool](http://www.scala-sbt.org/).
If you want to build from source, clone the repo and run `sbt assembly`.
The `jar` should be in the `target/` folder.
