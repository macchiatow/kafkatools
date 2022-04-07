package mt.gamify.kafkatools

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.sys.exit
import scala.collection.JavaConverters._

object KafkaToolsApp extends App {

  if (args.length == 0) {
    println("""Usage: kafkatools [--group group] [--producer] --bootstrap-server host:port --topic topic""");
    exit(1)
  }

  Options(args) match {
    // producer mode
    case Options(bootstrapServer, topic, _, true) =>
      val producer = new KafkaProducer(bootstrapServer)
      producer.publishRandom(topic, 10 seconds)
      producer.close()
    // consumer mode
    case Options(bootstrapServer, topic, _, _) =>
      val consumer = new KafkaConsumer(bootstrapServer, "test.group")

      consumer.checkConnectivity().failed.foreach { e =>
        println(e.getMessage)
        consumer.close()
        exit(1)
      }

      println("KafkaConsumer successfully connected")

      val overallProcessedRecords = new AtomicInteger(0)
      val startTime = System.currentTimeMillis()

      consumer.stream(topic, 1 minute)
        .filter(_.count() > 0)
        .foreach { rec =>
          overallProcessedRecords.addAndGet(rec.count())
          println(s"${rec.asScala.map(_.value()).mkString("\n")}")
        }

      consumer.close()
      println(s"KafkaConsumer process: ${overallProcessedRecords.get()} records")
      println(s"KafkaConsumer stayed connected: ${(System.currentTimeMillis() - startTime) / 1000} seconds")
  }

}
