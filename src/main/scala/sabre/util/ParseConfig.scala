package sabre.util

import scala.collection.immutable.Map
import scala.io.Source
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object ParseConfig {
  private val defaultConfig = "sabre.cfg"
  val (edgelistPath, masterServerAddress, server2Threads) = {
    val config = Source.fromFile(defaultConfig).getLines().map(_.trim).toVector
    val edgelist = "edgelists/" + config(0)
    val masterServerAddr = config(1).split("\\s+")(0)
    val s2t = config.tail.foldLeft(Map.empty[String, Option[Int]]) { (m, line) =>
      val linesplit = line.split("\\s+")
      if (linesplit.size == 2) m + (linesplit(0) -> Some(linesplit(1).toInt))
      else m + (linesplit(0) -> None)
    }
    (edgelist, masterServerAddr, s2t)
  }

  def readUndirectedGraph(): Graph[Int, UnDiEdge] = {
    val edgeSeq = Source.fromFile(edgelistPath).getLines().map { line =>
      val linesplit = line.split("\\s+")
      linesplit(0).toInt ~ linesplit(1).toInt
    }.toSeq
    Graph(edgeSeq: _*)
  }
}
