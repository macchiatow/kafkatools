package mt.gamify.kafkatools

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer => NativeKafkaConsumer}
import org.apache.kafka.common.errors.TimeoutException
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.KafkaException

import java.time.Duration
import java.util.Properties
import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Try}

class KafkaConsumer(brokerAddress: String, groupId: String) extends AutoCloseable with LazyLogging {

  private val consumerConfig = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "20000")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)

    props
  }

  lazy val consumer = new NativeKafkaConsumer[String, String](consumerConfig)

  def checkConnectivity(): Try[Unit] = {
    /**
     * KafkaConsumer::listTopics is one of few ways to check connectivity.
     * Kafka consumer can’t distinguish between temporary/permanent failure or misconfiguration
     */
    Try(consumer.listTopics(Duration.ofSeconds(5)))
      .recoverWith {
        case _: InterruptedException | _: TimeoutException | _: KafkaException =>
          Failure(new RuntimeException("Failed to connect within given timeout, broken is unavailable"))
      }
      .map(_ => ())
  }

  def stream(topic: String, duration: FiniteDuration): Stream[ConsumerRecords[String, String]] = {
    consumer.subscribe(List(topic).asJava)
    println("KafkaConsumer started streaming....")
    val startTime = System.currentTimeMillis()
    Stream.continually(())
      .takeWhile(_ => checkConnectivity().isSuccess && System.currentTimeMillis() - startTime < duration.toMillis)
      .map(_ => consumer.poll(Duration.ofMillis(1000)))
  }

  override def close(): Unit = consumer.close()
}
