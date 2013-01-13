package sabre.system

import akka.actor.{ Actor, ActorLogging }
import akka.remote.RemoteClientShutdown

class Watcher extends Actor with ActorLogging {
  override def receive = {
    case RemoteClientShutdown(_, _) =>
      log.error("Master down, shutting down.")
      context.system.shutdown()
    case _ =>
  }
}
