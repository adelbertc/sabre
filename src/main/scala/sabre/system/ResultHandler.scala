package sabre.system

import sabre.result.AbstractResult

object ResultHandler {
  case class HandleResult(result: AbstractResult)
  case object AllResultsSent
}
