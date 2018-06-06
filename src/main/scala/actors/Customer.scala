package actors

import java.util.Calendar

import actors.TweetAnalyzer.ReturnAnalysisResults
import akka.actor.{Actor, ActorRef, Props}

object Customer{

  def props(actor:ActorRef): Props = Props(new Customer(actor))


  final case class MessageMap(message: Map[Any,Any])
  case object  Perform
}

class Customer(val actor:ActorRef) extends Actor{
  import Customer._
  import context._
  import scala.concurrent.duration._


  def receive: PartialFunction[Any, Unit] = {

    case Perform =>
        actor ! ReturnAnalysisResults

    case message  =>
      system.scheduler.scheduleOnce(1.seconds , actor, ReturnAnalysisResults)
      val currentMinute = Calendar.getInstance().getTime
      println("")
      println("Time: "+ currentMinute+ " ==== "+message.toString+" ====")
      println("")
  }

}
