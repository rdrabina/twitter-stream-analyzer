package actors

import akka.actor.Props

import com.danielasfregola.twitter4s.entities.Tweet

object OccurrencesCounter{

  def props(keyword:String): Props = Props(new OccurrencesCounter(keyword))

  val introduction = "Count of tweets with given keyword"
}

class OccurrencesCounter(val keyword: String) extends TweetAnalyzer {

  import OccurrencesCounter._
  import java.util.concurrent.atomic._

  private val counter = new AtomicInteger(0)

  def analysisMethod(tweet : Tweet): Unit ={
    counter.getAndIncrement()
    ()
  }

  def introduceYourself() : String = {
    introduction+ " "+ keyword
  }

  def returnResults() : Map[String,AnyVal] = {
    Map(keyword -> counter.intValue())
  }

}
