package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import java.net.InetAddress
import scala.collection.mutable

object Reaper {
  val address = InetAddress.getLocalHost().getHostName()

  case class WatchMe(ref: ActorRef)
}

class Reaper extends Actor with ActorLogging {
  import Reaper._

  val watched = mutable.ArrayBuffer.empty[ActorRef]

  override def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) {
        log.info("Computation finished at " + address + ", shutting down.")
        context.system.shutdown()
      }
  }
}
