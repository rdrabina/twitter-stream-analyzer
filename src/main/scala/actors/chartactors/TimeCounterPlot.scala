package actors.chartactors


import javax.swing.JFrame

import actors.chartactors.TimeCounterPlot.StartPlot
import akka.actor.{Actor, Props}

import scalax.chart.api._

object TimeCounterPlot {
  def props(timePeriod:Int): Props = Props(new TimeCounterPlot(timePeriod))

  case object StartPlot
  final case class MessageMap(message:Map[Any,Any])
}

class TimeCounterPlot(val timePeriod:Int) extends Actor{

  import context._
  import scala.concurrent.duration._

  val series = new XYSeries("Result")
  val chart = XYLineChart(series)
  var x:Int = 0

  def receive: PartialFunction[Any, Unit] = {
    case StartPlot =>
      chart.show()
    case message:Map[String, AnyVal] =>
      system.scheduler.scheduleOnce(timePeriod.seconds) {
        val y: Number = message("result").asInstanceOf[Number]
        swing.Swing onEDT {
          series.add(x, y)
        }
        println("Time: "+x+"\t Result: "+y)
        x += timePeriod
      }

  }

}

