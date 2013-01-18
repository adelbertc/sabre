package sabre.util

import com.typesafe.config.ConfigFactory
import java.io.File
import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object FromEdgelist {
  val edgelistPath = ParseConfig.config.getString("sabre.graph")

  def edges(): Set[(Int, Int)] = {
    val edgelistFile = Source.fromFile(edgelistPath)
    val allEdges = edgelistFile.getLines().map { line =>
      val linesplit = line split "\\s+" map (_.toInt)
      (linesplit(0), linesplit(1))
    }.toSet
    // edgelistFile.close()
    allEdges
  }

  def nodes(): Set[Int] = {
    val edgelistFile = Source.fromFile(edgelistPath)
    val allNodes = edgelistFile.getLines().foldLeft(Set.empty[Int]) { (set, line) =>
      set ++ (line split "\\s+" map (_.toInt))
    }
    // edgelistFile.close()
    allNodes
  }

  def undirectedGraph(): Graph[Int, UnDiEdge] = {
    val edgelistFile = Source.fromFile(edgelistPath)
    val edgeSeq = edgelistFile.getLines().map { line =>
      val linesplit = line.split("\\s+")
      linesplit(0).toInt ~ linesplit(1).toInt
    }.toSeq
    // edgelistFile.close()
    Graph(edgeSeq: _*)
  }
}
