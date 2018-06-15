package actors

import java.util.Calendar

import actors.TweetAnalyzer.ReturnAnalysisResults
import actors.chartactors.TimeCounterPlot.StartPlot
import akka.actor.{Actor, ActorRef, Props}

object Customer{

  def props(timePeriod: Int, actor:ActorRef, plotActor:ActorRef): Props = Props(new Customer(timePeriod,actor, plotActor))


  final case class MessageMap(message: Map[Any,Any])
  case object  Perform
}

class Customer(val timePeriod: Int, val actor:ActorRef, val plotActor:ActorRef) extends Actor{
  import Customer._
  import context._
  import scala.concurrent.duration._


  def receive: PartialFunction[Any, Unit] = {

    case Perform =>
      actor ! ReturnAnalysisResults
      plotActor ! StartPlot

    case message:Map[String,AnyVal] =>
      system.scheduler.scheduleOnce(timePeriod.seconds , actor, ReturnAnalysisResults)
      plotActor ! message
      /*val currentMinute = Calendar.getInstance().getTime
      println("")
      println("Time: "+ currentMinute+ " ==== "+message.toString+" ====")
      println("")
      */
  }

}
