package actors

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import akka.actor.{Actor, ActorRef, Props}



/** Actor whose task is to connect to twitter's stream, read the tweets and distribute it to another actors.
  *
  */
object StreamingClient{
  //val consumerToken = ConsumerToken(key = "my-consumer-key", secret = "my-consumer-secret")
  //val accessToken = AccessToken(key = "my-access-key", secret = "my-access-secret")

  val consumerToken =
  ConsumerToken(
    key = "72Wsxiaa34YWUsvFiWeOYcYtc",
    secret = "mACzkZTpcn7NZVV3epLA4mT633kqhllICic5Eh7HWUnGujGYFH"
  )

  val accessToken =
    AccessToken(
      key = "981949032242253824-JSb3CAoTEfCR4Gl9klArll9NhHymORO",
      secret = "bYkRiddbTvGqQyjaYuxKG0qFKZRIa6ygOvz1mY32IyZzc"
    )

  def props(): Props =
    Props(
      new StreamingClient(
        TwitterStreamingClient(consumerToken, accessToken)
      )
    )

  final case class FilterTweetsByKeyword(keyword: String)

  final case class DistributeTweetToAnalyze(keyword: String, analyzers : ActorRef*)

  case object PrintSampleStream

  case object CloseStream


}

class StreamingClient(private val twitterStreamingClient: TwitterStreamingClient) extends Actor {

  import StreamingClient._
  import processingfunctions._

  def receive: PartialFunction[Any, Unit] = {
    case PrintSampleStream =>
      sender() ! "Ok"
      twitterStreamingClient
        .sampleStatuses(
          stall_warnings = true
        )(TwitterStreamProcessor.printTweetText)

    case FilterTweetsByKeyword(keyword) =>
      sender() ! keyword
      twitterStreamingClient
        .filterStatuses(
          tracks = Seq(keyword)
        )(TwitterStreamProcessor.printTweetText)

    case DistributeTweetToAnalyze(keyword,analyzers) =>
      sender() ! keyword
      twitterStreamingClient
      .filterStatuses(
        tracks = Seq(keyword)
      )(TwitterStreamProcessor.transferTweetTo(analyzers))

  }
}
