package actors.analyzingactors

import actors.TweetAnalyzer
import actors.TweetAnalyzer.Tack
import akka.actor.Props
import com.danielasfregola.twitter4s.entities.Tweet

object OccurrencesCounter{

  def props(keyword:String): Props = Props(new OccurrencesCounter(keyword))

  val introduction = "Count of tweets with given keyword"
}

class OccurrencesCounter(val keyword: String) extends TweetAnalyzer {

  import java.util.concurrent.atomic._

  import OccurrencesCounter._

  private val counter = new AtomicInteger(0)

  override def analysisMethod(tweet : Tweet): Unit ={
    counter.getAndIncrement()
    ()
  }

  override def introduceYourself() : String = {
    introduction+ " "+ keyword
  }

  override def returnResults() : Map[String,AnyVal] = {
    Map(keyword -> counter.intValue())
  }

  override def tickHandling(): Unit = {sender ! Tack ; () }

}
