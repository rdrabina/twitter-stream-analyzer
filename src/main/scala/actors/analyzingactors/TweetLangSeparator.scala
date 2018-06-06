package actors.analyzingactors

import actors.TweetAnalyzer
import akka.actor.Props
import com.danielasfregola.twitter4s.entities.Tweet
import scala.concurrent.ExecutionContext.Implicits.global


object TweetLangSeparator{

  def props(timer_sec:Int): Props =
    Props(new TimeCounter(timer_sec))

  val introduction = "Languages of tweet with given keyword"
}

class TweetLangSeparator(val timer_sec: Int) extends TweetAnalyzer {

  import TimeCounter._
  import scala.concurrent.duration._

  private val langMap = collection.mutable.Map[String, (Int,Int)]()

  private val tick =
    context.system.scheduler.schedule(1.seconds, timer_sec.seconds, self, TweetAnalyzer.Tick)

  override def postStop() :Unit = tick.cancel()

  override def tickHandling(): Unit = {
    langMap.mapValues[(Int,Int)]{ case (prev:Int,_:Int) => (0,prev) }
    ()
  }

  override def analysisMethod(tweet : Tweet): Unit ={
    println(tweet.lang)
  }

  override def introduceYourself() : String = {
    introduction
  }

  override def returnResults() : Map[String,AnyVal] = {
    Map( "HAHAH" -> 1321)
    //langMap.mapValues[Int]{ case (_:Int,actual:Int) => actual }.toMap
  }

}