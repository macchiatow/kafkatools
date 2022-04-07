package mt.gamify.kafkatools

import scala.annotation.tailrec
import scala.sys.exit

case class Options(bootstrapServer: String, topic: String, groupId: Option[String], isProducer: Boolean)

object Options {
  def apply(args: Array[String]): Options = {
    @tailrec
    def nextArg(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--bootstrap-server" :: value :: tail =>
          nextArg(map ++ Map("bootstrap-server" -> value), tail)
        case "--topic" :: value :: tail =>
          nextArg(map ++ Map("topic" -> value), tail)
        case "--group" :: value :: tail =>
          nextArg(map ++ Map("group" -> value), tail)
        case "--producer" :: tail =>
          nextArg(map ++ Map("producer" -> ""), tail)
        case unknown :: _ =>
          println("Unknown option " + unknown)
          exit(1)
      }
    }

    val options = nextArg(Map(), args.toList)
    new Options(
      bootstrapServer = options("bootstrap-server"),
      topic = options("topic"),
      groupId = options.get("group"),
      isProducer = options.contains("producer")
    )
  }
}
