import sabre.algorithm._
import sabre.system.Sabre
import sabre.util.FromEdgelist
import scala.annotation.switch
import scala.Console.err
import scala.collection.mutable
import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object ShortestPathAlgorithm extends AbstractAlgorithm {
  def getFullBfs(graph: Graph[Int, UnDiEdge], u: Int): Map[Int, Long] = {
    val bfs = mutable.Map.empty[Int, Long]
    val queue = mutable.Queue.empty[graph.NodeT]

    graph.nodes.foreach(node => bfs(node) = -1)
    bfs(u) = 0

    queue += graph.get(u)

    while (queue.nonEmpty) {
      val v = queue.dequeue()
      v.neighbors.foreach { vNeighbor =>
        if (bfs(vNeighbor) == -1) {
          bfs(vNeighbor) = bfs(v) + 1
          queue += vNeighbor
        }
      }
    }

    bfs.toMap
  }

  override def execute(graph: Graph[Int, UnDiEdge], input: Any): AbstractResult =
    MapResult(input, getFullBfs(graph, input.asInstanceOf[Int]))
}

object ShortestPath {
  def main(args: Array[String]): Unit = {
    val allNodes = FromEdgelist.nodes()
    if (args.size == 0)
      Sabre.execute(ShortestPathAlgorithm, allNodes)
    else Sabre.execute(ShortestPathAlgorithm, allNodes, args(0))
  }
}
