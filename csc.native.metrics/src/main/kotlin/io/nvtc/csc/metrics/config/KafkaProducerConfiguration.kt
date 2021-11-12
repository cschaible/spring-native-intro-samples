package io.nvtc.csc.metrics.config

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.MicrometerProducerListener

@Configuration
class KafkaProducerConfiguration {

  @Bean
  fun kafkaProducerFactory(
      kafkaProperties: KafkaProperties,
      meterRegistry: MeterRegistry
  ): DefaultKafkaProducerFactory<Any, Any?> =
      DefaultKafkaProducerFactory<Any, Any?>(
              kafkaProperties.buildProducerProperties().filter {
                !KafkaTopicConfiguration.IGNORE_PROPERTIES.contains(it.key)
              })
          .apply { addListener(MicrometerProducerListener(meterRegistry)) }
}
