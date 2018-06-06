package processingfunctions


import actors.TweetAnalyzer.AnalyzeTweet
import akka.actor.ActorRef
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage


object TwitterStreamProcessor {

  def printTweetText: PartialFunction[StreamingMessage, Unit] = {
    case tweet: Tweet => println(tweet.text)
  }


  def transferTweetTo(clients : ActorRef*): PartialFunction[StreamingMessage, Unit] = {
    case tweet: Tweet =>
      clients.foreach(act => act ! AnalyzeTweet(tweet))
  }

}