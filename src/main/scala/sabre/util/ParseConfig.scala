package sabre.util

import scala.io.Source

object ParseConfig {
  private val defaultConfig = "sabre.cfg"
  val (edgelistPath, masterServerAddress, server2Threads) = {
    val config = Source.fromFile(defaultConfig).getLines().map(_.trim).toVector
    val edgelist = "edgelists/" + config(0)
    val masterServerAddr = config(1).split("\\s+")(0)
    val s2t = config.tail.foldLeft(Map.empty[String, Option[Int]]) { (m, line) =>
      val linesplit = line.split("\\s+")
      val threads = linesplit(1).toInt
      require(threads >= 0)
      if (linesplit.size == 2) m + (linesplit(0) -> Some(threads))
      else m + (linesplit(0) -> None)
    }
    (edgelist, masterServerAddr, s2t)
  }
}
