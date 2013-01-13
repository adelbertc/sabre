package sabre.algorithm

import sabre.result.AbstractResult
import scalax.collection.Graph
import scalax.collection.GraphEdge._

trait AbstractAlgorithm extends Serializable {
  def execute(graph: Graph[Int, UnDiEdge], input: Any): AbstractResult
}
