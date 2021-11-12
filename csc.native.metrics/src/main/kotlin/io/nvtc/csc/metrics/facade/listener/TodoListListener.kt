package io.nvtc.csc.metrics.facade.listener

import io.nvtc.csc.metrics.config.KafkaTopicConfiguration.Companion.TOPIC_NAME
import io.nvtc.csc.metrics.model.TodoList
import java.util.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class TodoListListener {

  @KafkaListener(topics = [TOPIC_NAME])
  fun listen(record: ConsumerRecord<UUID, TodoList>, ack: Acknowledgment) {
    LOGGER.info("Todo list (identifier: ${record.key()}, todos: ${record.value().todos}")
    ack.acknowledge()
  }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(TodoListListener::class.java)
  }
}
