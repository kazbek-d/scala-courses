import java.util.HashMap

import Utilities._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

/**
  * Consumes messages from one or more topics in Kafka and does wordcount.
  * Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>
  *   <zkQuorum> is a list of one or more zookeeper servers that make quorum
  *   <group> is the name of kafka consumer group
  *   <topics> is a list of one or more kafka topics to consume from
  *   <numThreads> is the number of threads the kafka consumer should use
  *
  * Example:
  *    `$ bin/run-example \
  *      org.apache.spark.examples.streaming.KafkaWordCount zoo01,zoo02,zoo03 \
  *      my-consumer-group topic1,topic2 1`
  */
object KafkaWordCount extends App {
  setupLogging()

  val zkQuorum = "172.17.0.2:2181"
  val group = "default"
  val topics = "test"
  val numThreads = 1

  val tempFolder = "/home/kdzarasov/tmp/"
  val ssc = new StreamingContext("local[*]", "KafkaWordCount", Milliseconds(500))


  val topicMap = topics.split(",").map((_, numThreads)).toMap
  val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
  val words = lines.flatMap(_.split(" "))
  val wordCounts = words.map(x => (x, 1L))
    .reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)
  wordCounts.print()

  ssc.checkpoint(tempFolder + "checkpoint")
  ssc.start()
  ssc.awaitTermination()
}



// Produces some random words between 1 and 100.
object KafkaWordCountProducer extends App {

  //  Usage: KafkaWordCountProducer <metadataBrokerList> <topic> "<messagesPerSec> <wordsPerMessage>"
  val brokers = "172.17.0.3:9092"
  val topic = "test"
  val messagesPerSec = "1"
  val wordsPerMessage = "4"

  // Zookeeper connection properties
  val props = new HashMap[String, Object]()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  // Send some messages
  while (true) {
    (1 to messagesPerSec.toInt).foreach { messageNum =>
      val str = (1 to wordsPerMessage.toInt).map(x => scala.util.Random.nextInt(10).toString)
        .mkString(" ")

      val message = new ProducerRecord[String, String](topic, null, str)
      producer.send(message)
    }

    Thread.sleep(1000)
  }

}
