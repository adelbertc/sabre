package sabre.util

import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object FromEdgelist {
  def edges(): Set[(Int, Int)] = Source.fromFile(ParseConfig.edgelistPath).getLines().map { line =>
    val linesplit = line.split("\\s+").map(_.toInt)
    (linesplit(0), linesplit(1))
  }.toSet

  def nodes(): Set[Int] = {
    Source.fromFile(ParseConfig.edgelistPath).getLines().foldLeft(Set.empty[Int]) { (set, line) =>
      set ++ (line split "\\s+" map (_.toInt))
    }
  }
  def undirectedGraph(): Graph[Int, UnDiEdge] = {
    val edgeSeq = Source.fromFile(ParseConfig.edgelistPath).getLines().map { line =>
      val linesplit = line.split("\\s+")
      linesplit(0).toInt ~ linesplit(1).toInt
    }.toSeq
    Graph(edgeSeq: _*)
  }
}
