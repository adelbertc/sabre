package sabre.app

import sabre.algorithm._
import sabre.system.Sabre
import sabre.util.NodeGetter
import scala.Console.err
import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object DegreeAlgorithm extends AbstractAlgorithm {
  override def execute(graph: Graph[Int, UnDiEdge], input: Any): Option[AbstractResult] = input match {
    case u: Int =>
      graph.find(u).map(node => Result(input, node.degree))
    /*
      graph.get(u).degree

      val uNode = graph find u
      uNode match {
        case None => None
        case _ => Some(Result(input, uNode.get.degree))
      }
      */

    case _ => None
  }
}

object DegreeApp {
  def main(args: Array[String]): Unit = {
    if (args.size == 0)
      Sabre.execute(DegreeAlgorithm, NodeGetter.getAllNodes())
    else Sabre.execute(DegreeAlgorithm, NodeGetter.getAllNodes(), args(0))
  }
}
