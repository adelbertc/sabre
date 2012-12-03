package sabre.system

import akka.actor.{ Actor, ActorLogging, ActorPath, ActorRef, ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import sabre.algorithm._
import sabre.system.MasterWorkerProtocol._
import sabre.system.WorkerResultHandlerProtocol._
import sabre.util.ParseConfig
import scala.Console.err
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

object Worker {
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
    // val availableProcessors = 1
    val hostname = InetAddress.getLocalHost().getHostName()
    val numberOfThreads = ParseConfig.server2Threads(hostname).getOrElse(availableProcessors)
    val masterServerAddress = ParseConfig.masterServerAddress

    val system = ActorSystem("Worker", ConfigFactory.load(workerAkkaConfig))
    val graph = ParseConfig.readUndirectedGraph()
    val masterLocation = "akka://Sabre@" + masterServerAddress + ":2554/user/master"
    val master = system.actorFor(masterLocation)

    println("Master location: " + master)

    for (i <- 0 until numberOfThreads) {
      val workerName = "worker" + i
      system.actorOf(Props(new Worker(graph, master)), workerName)
    }

    println(numberOfThreads + " worker(s) started on " + hostname)
  }
}

class Worker(graph: Graph[Int, UnDiEdge], master: ActorRef) extends Actor with ActorLogging {
  var algorithm: Option[AbstractAlgorithm] = None

  case object WorkComplete

  def doWork(resultHandler: ActorRef, work: Any) {
    Future {
      val result = algorithm.get.execute(graph, work)
      result match {
        case None => log.error("Algorithm computation received invalid input.")
        case Some(res) => resultHandler ! HandleResult(res)
      }
      self.tell(WorkComplete, self)
    }
    /*
    future {
      algorithm.get.execute(graph, work)
    } onSuccess {
      case None => 
        log.error("Algorithm computation received invalid input.")
        self.tell(WorkComplete, self)
      case Some(result) =>
        resultHandler ! HandleResult(result)
        self.tell(WorkComplete, self)
    }
    */
  }

  override def preStart() = master ! WorkerCreated(self)

  // def working(work: Any): Receive = {
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
    case NoWorkToBeDone =>
  }

  def receive = idle
}
