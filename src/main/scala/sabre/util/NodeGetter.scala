package sabre.util

import sabre.algorithm._
import scala.collection.immutable
import scala.io.Source

object NodeGetter {
  def getAllNodes(): Iterator[Int] = {
    Source.fromFile(ParseConfig.edgelistPath).getLines().foldLeft(Set.empty[Int]) { (set, line) =>
      set ++ (line split "\\s+" map (_.toInt))
    }.iterator
  }
}
