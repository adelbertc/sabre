package sabre.util

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.io.Resource

object FromEdgelist {
  private val edgelistPath = ParseConfig.config.getString("sabre.graph")

  def edges(): Set[(Int, Int)] =
    Resource.fromFile(edgelistPath).lines().map { line =>
      val linesplit = line split "\\s+" map (_.toInt)
      (linesplit(0), linesplit(1))
    }.toSet

  def nodes(): Set[Int] =
    Resource.fromFile(edgelistPath).lines().foldLeft(Set.empty[Int]) { (set, line) =>
      set ++ (line split "\\s+" map (_.toInt))
    }

  def undirectedGraph(): Graph[Int, UnDiEdge] = {
    val edgeSeq = Resource.fromFile(edgelistPath).lines().map { line =>
      val linesplit = line.split("\\s+")
      linesplit(0).toInt ~ linesplit(1).toInt
    }.toSeq
    Graph(edgeSeq: _*)
  }
}
