package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }

object Watcher {
  case class Watch(worker: ActorRef)
}

class Watcher extends Actor with ActorLogging {
  import Watcher._

  var numberOfWorkers = 0

  override def receive = {
    case Watch(worker) =>
      context.watch(worker)
      numberOfWorkers += 1
    case Terminated(_) =>
      numberOfWorkers -= 1
      if (numberOfWorkers == 0) {
        log.info("All workers killed, shutting down system.")
        context.system.shutdown()
      }
  }
}
