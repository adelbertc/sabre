package sabre.system

import akka.actor.ActorRef
import sabre.algorithm._

object SabreMasterProtocol {
  case class DistributeWork(work: Any)
  case object AllWorkSent
}

object MasterWorkerProtocol {
  // Messages from Workers
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef)

  // Messages to Workers
  case class DoAlgorithm(algorithm: AbstractAlgorithm)
  case class WorkToBeDone(work: Any)
  case object WorkIsReady
  case object NoWorkToBeDone
}

object WorkerResultHandlerProtocol {
  case class HandleResult(result: AbstractResult)
}

object MasterResultHandlerProtocol {
  case object AllResultsSent
}
