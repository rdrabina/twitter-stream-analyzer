package actors.chartactors

import actors.chartactors.TimeCounterPlot.StartPlot
import akka.actor.{Actor, Props}

import scalax.chart.api._

object CounterPlot {
  def props(timePeriod:Int, keyWord: String):Props = Props(new CounterPlot(timePeriod, keyWord))
}

class CounterPlot(val timePeriod:Int, val keyWord:String) extends Actor {

  import context._
  import scala.concurrent.duration._

  val series = new XYSeries("Result")
  val chart = XYLineChart(series)
  var x: Int = 0

  def receive: PartialFunction[Any, Unit] = {
    case StartPlot =>
      chart.show()
    case message: Map[String, AnyVal] =>
      system.scheduler.scheduleOnce(timePeriod.seconds) {
        val y: Number = message(keyWord).asInstanceOf[Number]
        swing.Swing onEDT {
          series.add(x, y)
        }
        println("Time: "+x+"\t Result: "+y)
        x += timePeriod
      }
  }
}
