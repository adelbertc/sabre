import sabre.algorithm._
import sabre.result.Result
import sabre.system.Sabre
import sabre.util.FromEdgelist
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object DegreeAlgorithm extends AbstractAlgorithm {
  override def execute(graph: Graph[Int, UnDiEdge], input: Any): AbstractResult =
    Result(input, graph.get(input.asInstanceOf[Int]).degree)
}

object Degree {
  def main(args: Array[String]) {
    val allNodes = FromEdgelist.nodes()
    if (args.size == 0)
      Sabre.execute(DegreeAlgorithm, allNodes) 
    else Sabre.execute(DegreeAlgorithm, allNodes, args(0))
  }
}
