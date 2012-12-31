import sabre.algorithm._
import sabre.system.Sabre
import sabre.util.NodeGetter
import scala.Console.err
import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object DegreeAlgorithm extends AbstractAlgorithm {
  override def execute(graph: Graph[Int, UnDiEdge], input: Any): AbstractResult =
    Result(input, graph.get(input.asInstanceOf[Int]).degree)
}

object Degree {
  def main(args: Array[String]): Unit = {
    if (args.size == 0)
      Sabre.execute(DegreeAlgorithm, NodeGetter.getAllNodes())
    else Sabre.execute(DegreeAlgorithm, NodeGetter.getAllNodes(), args(0))
  }
}
