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
  import scalax.chart.api._

  val series = new XYSeries("My plot")
  val chart = XYLineChart(series)
  var x = 1;
  def receive: PartialFunction[Any, Unit] = {

    case Perform =>

      chart.show()
      actor ! ReturnAnalysisResults

    case message:Map[String,AnyVal] =>
      system.scheduler.scheduleOnce(1.seconds , actor, ReturnAnalysisResults)
      val currentMinute = Calendar.getInstance().getTime
      val y:Number = message.get("result").get.asInstanceOf[Number]
      swing.Swing onEDT {
        series.add(x,y)
      }
      println("")
      println("Time: "+ currentMinute+ " ==== "+message.toString+" ====")
      println("")
      x+=1;
  }

}
