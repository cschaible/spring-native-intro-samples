package io.nvtc.csc.metrics.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaAdmin

@Configuration
@Profile("!test")
class KafkaTopicConfiguration {

  @Bean
  fun kafkaAdmin(kafkaProperties: KafkaProperties) =
      KafkaAdmin(
          kafkaProperties.buildAdminProperties().filter { !IGNORE_PROPERTIES.contains(it.key) })

  @Bean fun kafkaTopic() = NewTopic(TOPIC_NAME, TOPIC_PARTITIONS, 1)

  companion object {
    const val TOPIC_NAME = "native-sample-topic"
    const val TOPIC_PARTITIONS = 2
    val IGNORE_PROPERTIES = arrayOf("value.subject.name.strategy", "key.subject.name.strategy")
  }
}
