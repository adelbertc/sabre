package sabre.app

import sabre.algorithm._
import sabre.system.Sabre
import scala.Console.err
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.io.Source
import scala.util.Random
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object BiasedHittingTimeAlgorithm extends AbstractAlgorithm {
  val iterLimit = 2000

  override def execute(graph: Graph[Int, UnDiEdge], input: Any): Option[AbstractResult] = input match {
    case pair: (_, _) =>
      val random = ThreadLocalRandom.current()
      val actualPair = pair.asInstanceOf[(Int, Int)]
      var walker = graph.get(actualPair._1)
      val dest = actualPair._2

      println(walker.edges)

      var hopCount = 0L
      var i = 0
      while (i != 2000) {
        while (walker != dest) {
          walker = walker.edges.draw(random)._2
          hopCount += 1L
        }
        i += 1
      }
      val averagedHittingTime = hopCount / 2000.0
      println(averagedHittingTime)
      Some(VectorResult(input, Vector(averagedHittingTime)))

    case _ => None
  }
}

object BiasedHittingTimeApp {
  def main(args: Array[String]) {
    if (args.length == 0) {
      err.println("You must provide a two columned file of src -> dest pairs.")
      return
    }

    val pairs = Source.fromFile(args(0)).getLines().map { line =>
      val linesplit = line.split("\\s+")
      val src = linesplit(0).toInt
      val dest = linesplit(1).toInt
      (src, dest)
    }.toVector

    if (args.size == 1)
      Sabre.execute(BiasedHittingTimeAlgorithm, pairs)
    else Sabre.execute(BiasedHittingTimeAlgorithm, pairs, args(1))
  }
}
