package actors

import akka.actor.Actor
import com.danielasfregola.twitter4s.entities.Tweet


object TweetAnalyzer{

  final case class AnalyzeTweet(tweet: Tweet)

  case object IntroduceYourself

  case object ReturnAnalysisResults

}

abstract class TweetAnalyzer extends Actor{

  import TweetAnalyzer._

  def analysisMethod(tweet : Tweet)

  def introduceYourself() : String

  def returnResults() : Map[String,AnyVal]

  def receive: PartialFunction[Any, Unit] = {
    case IntroduceYourself =>
      sender() ! introduceYourself()

    case ReturnAnalysisResults =>
      sender() ! returnResults()

    case AnalyzeTweet(tweet: Tweet) =>
      analysisMethod(tweet)
  }

}
