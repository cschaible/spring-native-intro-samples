package io.nvtc.csc.logging.service

import io.nvtc.csc.logging.model.Todo
import io.nvtc.csc.logging.model.TodoList
import java.util.*
import java.util.UUID.randomUUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TodoListService(private val todoListRepository: MutableList<TodoList> = ArrayList()) {

  fun findByIdentifier(identifier: UUID): TodoList? =
      todoListRepository.firstOrNull {
        LOGGER.debug("Find list (identifier: $identifier)")
        it.identifier == identifier
      }

  fun create(identifier: UUID?, todos: List<Todo>) =
      todoListRepository.let {
        val list = TodoList(identifier ?: randomUUID(), todos)
        it.add(list)
        LOGGER.debug("List added (identifier: $identifier)")
        list
      }

  fun update(list: TodoList) =
      todoListRepository.let {
        todoListRepository.removeIf { it.identifier == list.identifier }
        todoListRepository.add(list)
        LOGGER.debug("List updated (identifier: ${list.identifier})")
        list
      }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(TodoListService::class.java)
  }
}
