package mt.gamify.kafkatools

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord, KafkaProducer => NativeKafkaProducer}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

class KafkaProducer(brokerAddress: String) extends AutoCloseable with LazyLogging {

  private val producerConfig = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "KAFKA_TOOLS_CLIENT_ID")

    props
  }

  private val producer = new NativeKafkaProducer[String, String](producerConfig)

  def publishRandom(topic: String, duration: FiniteDuration): Unit = {
    val startTime = System.currentTimeMillis()

    while (System.currentTimeMillis() - startTime < duration.toMillis) {
      val message = Random.alphanumeric.take(10).mkString

      val record = new ProducerRecord[String, String](topic, message)
      println(s"Sending message to the $topic => $message")
      producer.send(record).get(1, TimeUnit.MINUTES)
    }

    println("KafkaProducer is done")
  }

  override def close(): Unit = producer.close()

}
