package actors.analyzingactors

import actors.TweetAnalyzer
import actors.TweetAnalyzer.Tack
import akka.actor.Props
import com.danielasfregola.twitter4s.entities.Tweet

import scala.concurrent.ExecutionContext.Implicits.global


object TweetLangSeparator{

  def props(timer_sec:Int): Props =
    Props(new TweetLangSeparator(timer_sec))

  val introduction = "Languages of tweet with given keyword"
}

class TweetLangSeparator(val timer_sec: Int) extends TweetAnalyzer {

  import TimeCounter._
  import scala.concurrent.duration._

  private val langMap = collection.mutable.HashMap[String, (Int,Int)]()

  private val tick =
    if (timer_sec > 0)
      context.system.scheduler.schedule(1.seconds, timer_sec.seconds, self, TweetAnalyzer.Tick)
    else
      context.system.scheduler.scheduleOnce(0.seconds, self, TweetAnalyzer.Tick)

  override def postStop() :Unit = tick.cancel()

  override def tickHandling(): Unit = {
    langMap.transform{ case (_,(prev:Int,_:Int)) => (0,prev) }
    sender ! Tack
    ()
  }

  override def analysisMethod(tweet : Tweet): Unit ={
    val l = tweet.lang
    l match {
      case Some(lang) =>
          langMap.get(lang) match {
            case Some((left,right)) =>
              langMap.update(lang,(left+1,right))
            case None =>
              langMap.put(lang,(1,0))
        }
      case None =>()
    }
  }

  override def introduceYourself() : String = {
    introduction
  }

  override def returnResults() : Map[String,AnyVal] = {
    if(timer_sec > 0)
      langMap.mapValues[Int]{ case (_:Int,actual:Int) => actual }.toMap
    else
      langMap.mapValues[Int]{ case (actual:Int,_:Int) => actual }.toMap
  }

}