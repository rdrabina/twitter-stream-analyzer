package actors.chartactors

import actors.chartactors.TimeCounterPlot.StartPlot
import akka.actor.{Actor, Props}

import scalax.chart.api._

object LangPlot{
  def props(timePeriod:Int): Props = Props(new LangPlot(timePeriod))
}

class LangPlot(val timePeriod:Int) extends Actor {

  import context._
  import scala.concurrent.duration._

  val und: String = "Undefined"
  val fr: String = "French"
  val es: String = "Spanish"
  val pt: String = "Portuguese"
  val pl: String = "Polish"


  val seriesUnd = new XYSeries(und)
  val seriesFr = new XYSeries(fr)
  val seriesEs = new XYSeries(es)
  val seriesPt = new XYSeries(pt)
  val seriesPl = new XYSeries(pl)
  //val chart = XYLineChart(seriesUnd)
  val chart = XYLineChart(Seq(seriesUnd, seriesFr, seriesEs, seriesPt, seriesPl))
  var x: Int = 0

  var yUnd: Number = 0
  var yFr: Number = 0
  var yEs: Number = 0
  var yPt: Number = 0
  var yPl: Number = 0

  def receive: PartialFunction[Any, Unit] = {
    case StartPlot =>
      chart.show()
    case message:Map[String, AnyVal] =>
      system.scheduler.scheduleOnce(timePeriod.seconds) {
        if (message.contains("und")){
          yUnd = message("und").asInstanceOf[Number]
          seriesUnd.add(x, yUnd)
          println("Time: "+x+"\t Language: "+und+"\t Result: "+yUnd)
        }
        if (message.contains("fr")){
          yFr = message("fr").asInstanceOf[Number]
          seriesFr.add(x, yFr)
          println("Time: "+x+"\t Language: "+fr+"\t Result: "+yFr)
        }
        if (message.contains("es")){
          yEs = message("es").asInstanceOf[Number]
          seriesEs.add(x, yEs)
          println("Time: "+x+"\t Language: "+es+"\t Result: "+yEs)
        }
        if (message.contains("pt")){
          yPt = message("pt").asInstanceOf[Number]
          seriesPt.add(x, yPt)
          println("Time: "+x+"\t Language: "+pt+"\t Result: "+yPt)
        }
        if (message.contains("pl")){
          yPl = message("pl").asInstanceOf[Number]
          seriesPl.add(x, yPl)
          println("Time: "+x+"\t Language: "+pl+"\t Result: "+yPl)
        }
        println("")
        x += timePeriod
      }

  }
}
