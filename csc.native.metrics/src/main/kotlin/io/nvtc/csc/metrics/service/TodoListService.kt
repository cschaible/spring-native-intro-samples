package io.nvtc.csc.metrics.service

import io.nvtc.csc.metrics.config.KafkaTopicConfiguration.Companion.TOPIC_NAME
import io.nvtc.csc.metrics.config.KafkaTopicConfiguration.Companion.TOPIC_PARTITIONS
import io.nvtc.csc.metrics.model.TodoList
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Utils
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class TodoListService(
    private val kafkaTemplate: KafkaTemplate<Any, Any?>,
) {

  fun update(list: TodoList) =
      with(list) {
        kafkaTemplate
            .send(ProducerRecord(TOPIC_NAME, partition(this.identifier), this.identifier, this))
            .get()
      }
          .let { list }

  private fun partition(identifier: UUID) =
      Utils.toPositive(Utils.murmur2(identifier.toString().toByteArray(UTF_8))) % TOPIC_PARTITIONS
}
