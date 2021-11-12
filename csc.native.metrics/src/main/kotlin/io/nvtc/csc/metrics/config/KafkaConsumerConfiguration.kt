package io.nvtc.csc.metrics.config

import io.micrometer.core.instrument.MeterRegistry
import io.nvtc.csc.metrics.config.KafkaTopicConfiguration.Companion.IGNORE_PROPERTIES
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.MicrometerConsumerListener
import org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL

@Configuration
class KafkaConsumerConfiguration {

  @Bean
  fun kafkaConsumerFactory(
      kafkaProperties: KafkaProperties,
      meterRegistry: MeterRegistry
  ): DefaultKafkaConsumerFactory<Any, Any?> =
      DefaultKafkaConsumerFactory<Any, Any?>(
              kafkaProperties.buildConsumerProperties().filter {
                !IGNORE_PROPERTIES.contains(it.key)
              })
          .apply { addListener(MicrometerConsumerListener(meterRegistry)) }

  @Bean
  fun kafkaListenerContainerFactory(
      kafkaProperties: KafkaProperties,
      kafkaConsumerFactory: DefaultKafkaConsumerFactory<Any, Any?>
  ) =
      ConcurrentKafkaListenerContainerFactory<Any, Any?>().apply {
        containerProperties.ackMode = MANUAL
        containerProperties.isMissingTopicsFatal = false
        consumerFactory = kafkaConsumerFactory
        setConcurrency(kafkaProperties.listener.concurrency)
      }
}
