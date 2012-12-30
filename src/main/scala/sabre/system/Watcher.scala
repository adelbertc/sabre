package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import akka.remote.RemoteClientShutdown

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
    case RemoteClientShutdown(_, _) =>
      log.error("Master down, shutting down.")
      context.system.shutdown()
    case _ =>
  }
}
