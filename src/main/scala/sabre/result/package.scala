package sabre

import scala.language.existentials

package object result {
  trait AbstractResult extends Serializable {
    override def toString = "trait AbstractResult"
  }

  case class MapResult(input: Any, output: Map[_ <: Any, Any]) extends AbstractResult {
    override def toString = output.map { p: (Any, Any) => input + " " + p._1 + " " + p._2 }.mkString("\n")
  }

  case class MapIterableResult(input: Any, output: Map[_ <: Any, Iterable[Any]]) extends AbstractResult {
    override def toString = output.map { p: (Any, Iterable[Any]) => input + " " + p._1 + " " + p._2.mkString(" ") }.mkString("\n")
  }

  case object NullResult extends AbstractResult {
    override def toString = ""
  }

  case class Result(input: Any, output: Any) extends AbstractResult {
    override def toString = input + " " + output
  }

  case class SingleResult(output: Any) extends AbstractResult {
    override def toString = output.toString
  }

  case class SingleMapResult(output: Map[_ <: Any, Any]) extends AbstractResult {
    override def toString = output.map { p: (Any, Any) => p._1 + " " + p._2 }.mkString("\n")
  }

  case class SingleMapIterableResult(output: Map[_ <: Any, Iterable[Any]]) extends AbstractResult {
    override def toString = output.map { p: (Any, Iterable[Any]) => p._1 + " " + p._2.mkString("\n") }.mkString("\n")
  }

  case class SingleIterableResult(output: Iterable[Any]) extends AbstractResult {
    override def toString = output.mkString(" ")
  }

  case class TupleInputResult(input: (Any, Any), output: Any) extends AbstractResult {
    override def toString = input._1 + " " + input._2 + " " + output
  }

  case class IterableResult(input: Any, output: Iterable[Any]) extends AbstractResult {
    override def toString = input + " " + output.mkString(" ")
  }
}
