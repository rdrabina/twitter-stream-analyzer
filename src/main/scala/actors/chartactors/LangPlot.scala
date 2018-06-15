package actors.chartactors

import actors.chartactors.TimeCounterPlot.StartPlot
import akka.actor.{Actor, Props}

import scalax.chart.api._

object LangPlot{
  def props(timePeriod:Int, map:scala.collection.mutable.Map[String,Boolean]): Props = Props(new LangPlot(timePeriod,map))
}

class LangPlot(val timePeriod:Int,map:scala.collection.mutable.Map[String,Boolean]) extends Actor {

  import context._
  import scala.concurrent.duration._

  val en: String = "English"
  val fr: String = "French"
  val es: String = "Spanish"
  val pt: String = "Portuguese"
  val pl: String = "Polish"
  val de: String = "German"


  val seriesEn = new XYSeries(en)
  val seriesFr = new XYSeries(fr)
  val seriesEs = new XYSeries(es)
  val seriesPt = new XYSeries(pt)
  val seriesPl = new XYSeries(pl)
  val seriesDe = new XYSeries(de)

  val chart = XYLineChart(Seq(seriesEn, seriesFr, seriesEs, seriesPt, seriesPl,seriesDe))
  var x: Int = 0

  var yEn: Number = 0
  var yFr: Number = 0
  var yEs: Number = 0
  var yPt: Number = 0
  var yPl: Number = 0
  var yDe: Number = 0

  def receive: PartialFunction[Any, Unit] = {
    case StartPlot =>
      chart.show()
    case message:Map[String, AnyVal] =>
      system.scheduler.scheduleOnce(timePeriod.seconds) {
        if (message.contains("en") && map.contains(en)&& map(en)){
          yEn = message("en").asInstanceOf[Number]
          //seriesEn.add(x, yEn)
          println("Time: "+x+"\t Language: "+en+"\t Result: "+yEn)
        }
        if (message.contains("fr") && map.contains(fr)&& map(fr)){
          yFr = message("fr").asInstanceOf[Number]
          //seriesFr.add(x, yFr)
          println("Time: "+x+"\t Language: "+fr+"\t Result: "+yFr)
        }
        if (message.contains("es") && map.contains(es) && map(es)){
          yEs = message("es").asInstanceOf[Number]
          //seriesEs.add(x, yEs)
          println("Time: "+x+"\t Language: "+es+"\t Result: "+yEs)
        }
        if (message.contains("pt") && map.contains(pt) && map(pt)){
          yPt = message("pt").asInstanceOf[Number]
          //seriesPt.add(x, yPt)
          println("Time: "+x+"\t Language: "+pt+"\t Result: "+yPt)
        }
        if (message.contains("pl") && map.contains(pl) && map(pl)){
          yPl = message("pl").asInstanceOf[Number]
          //seriesPl.add(x, yPl)
          println("Time: "+x+"\t Language: "+pl+"\t Result: "+yPl)
        }
        if (message.contains("de") && map.contains(de) && map(de)){
          yDe = message("de").asInstanceOf[Number]
          //seriesPl.add(x, yDe)
          println("Time: "+x+"\t Language: "+de+"\t Result: "+yDe)
        }
        swing.Swing onEDT {
          seriesPl.add(x, yDe)
          seriesPl.add(x, yPl)
          seriesPt.add(x, yPt)
          seriesEs.add(x, yEs)
          seriesFr.add(x, yFr)
          seriesEn.add(x, yEn)

        }
        println("")
        x += timePeriod
      }

  }
}
