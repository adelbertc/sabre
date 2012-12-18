package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorPath, ActorRef, ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import sabre.algorithm._
import sabre.system.Master._
import sabre.system.ResultHandler._
import sabre.system.Watcher._
import sabre.util.ParseConfig
import scala.Console.err
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object Worker {
  case class DoAlgorithm(algorithm: AbstractAlgorithm)
  case class WorkToBeDone(work: Any)
  case object WorkIsReady
  case object NoWorkToBeDone

  val workerAkkaConfig = ConfigFactory.parseString("""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
        serialization-bindings {
          "scalax.collection.Graph" = java
        }
      }
      remote {
        netty {
          hostname = """ + "\"" + InetAddress.getLocalHost().getHostName() + "\"" + """
          message-frame-size = 500 MiB
          port = 2552
        }
      }
    }
  """)

  def main(args: Array[String]) {
    val availableProcessors = Runtime.getRuntime().availableProcessors()
    val hostname = InetAddress.getLocalHost().getHostName()
    val numberOfThreads = ParseConfig.server2Threads(hostname).getOrElse(availableProcessors)
    val masterServerAddress = ParseConfig.masterServerAddress

    val system = ActorSystem("Worker", ConfigFactory.load(workerAkkaConfig))
    val graph = ParseConfig.readUndirectedGraph()
    val masterLocation = "akka://Sabre@" + masterServerAddress + ":2554/user/master"
    val master = system.actorFor(masterLocation)

    val watcher = system.actorOf(Props[Watcher])

    println("Master location: " + master)

    for (i <- 0 until numberOfThreads) {
      val workerName = "worker" + i
      val workerRef = system.actorOf(Props(new Worker(graph, master, watcher)), workerName)
    }

    println(numberOfThreads + " worker(s) started on " + hostname)
  }
}

class Worker(graph: Graph[Int, UnDiEdge], master: ActorRef, watcher: ActorRef) extends Actor with ActorLogging {
  import Worker._
  case object WorkComplete

  var algorithm: Option[AbstractAlgorithm] = None

  def doWork(resultHandler: ActorRef, work: Any) {
    future {
      val result = algorithm.get.execute(graph, work)
      result match {
        case None => log.error("Algorithm received bad input.")
        case Some(res) => resultHandler ! HandleResult(res)
      }
      self.tell(WorkComplete, self)
    }
  }

  override def preStart() = {
    master ! WorkerCreated(self)
    watcher ! Watch(self)
  }

  def working: Receive = {
    case WorkIsReady =>
    case NoWorkToBeDone =>
    case WorkToBeDone =>
      log.error("Received work while working.")
    case WorkComplete =>
      master ! WorkIsDone(self)
      master ! WorkerRequestsWork(self)
      context.become(idle)
  }

  def idle: Receive = {
    case DoAlgorithm(alg) =>
      algorithm = Some(alg)
    case WorkIsReady =>
      master ! WorkerRequestsWork(self)
    case WorkToBeDone(work) =>
      algorithm match {
        case None =>
          log.error("Got work {} without algorithm!", work)
        case Some(alg) =>
          doWork(sender, work)
          context.become(working)
      }
    case NoWorkToBeDone => context.stop(self)
  }

  override def receive = idle
  /*
  override def receive = {
    case DoAlgorithm(alg) => algorithm = Some(alg)
    case WorkIsReady =>
      master ! WorkerRequestsWork(self)
    case WorkToBeDone(work) =>
      doWork(sender, work)
      context.become(working)
    case WorkComplete =>
      master ! WorkIsDone
      master ! WorkerRequestsWork(self)
    case NoWorkToBeDone => context.stop(self)
  }
  */
}
