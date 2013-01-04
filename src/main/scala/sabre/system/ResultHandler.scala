package sabre.system

import sabre.algorithm.AbstractResult

object ResultHandler {
  case class HandleResult(result: AbstractResult)
  case object AllResultsSent
}
