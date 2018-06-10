package actors.analyzingactors

import actors.TweetAnalyzer
import actors.TweetAnalyzer.Tack
import akka.actor.Props
import com.danielasfregola.twitter4s.entities.Tweet

import scala.concurrent.ExecutionContext.Implicits.global

object TimeCounter{

  class InvalidTimerException(errorMessage:String) extends Exception(errorMessage){}

  def props(timer_sec:Int): Props =
    Props(
      if (timer_sec <= 0) {
        throw new InvalidTimerException("Timer have to be bigger than 0!")
      }
      else{
        new TimeCounter(timer_sec)
      }
    )

  val introduction = "Count of tweets from stream with given keyword from last time (timer)"
}

class TimeCounter(val timer_sec: Int) extends TweetAnalyzer {

  import java.util.concurrent.atomic._

  import TimeCounter._

  import scala.concurrent.duration._

  private val counter = new AtomicInteger(0)
  private val lastSecCountValue = new AtomicInteger(0)

  private val tick =
    context.system.scheduler.schedule(1.seconds, timer_sec.seconds, self, TweetAnalyzer.Tick)

  override def postStop() :Unit = tick.cancel()

  override def tickHandling(): Unit = {
    val tmp = counter.getAndSet(0)
    lastSecCountValue.set(tmp)
    sender ! Tack
    ()
  }

  override def analysisMethod(tweet : Tweet): Unit ={
    counter.getAndIncrement()
    ()
  }

  override def introduceYourself() : String = {
    introduction
  }

  override def returnResults() : Map[String,AnyVal] = {
    Map( "result" -> lastSecCountValue.intValue())
  }

}