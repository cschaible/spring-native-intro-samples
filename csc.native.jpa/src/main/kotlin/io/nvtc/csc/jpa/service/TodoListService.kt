package io.nvtc.csc.jpa.service

import io.nvtc.csc.jpa.model.Todo
import io.nvtc.csc.jpa.model.TodoList
import io.nvtc.csc.jpa.repository.TodoListRepository
import java.util.*
import java.util.UUID.randomUUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoListService(private val todoListRepository: TodoListRepository) {

  @Transactional(readOnly = true)
  fun findByIdentifier(identifier: UUID): TodoList? =
      todoListRepository.findByIdentifier(identifier)

  @Transactional
  fun create(identifier: UUID?, todos: List<Todo>) =
      todoListRepository.save(TodoList(identifier ?: randomUUID(), todos))

  @Transactional fun update(list: TodoList) = todoListRepository.save(list)
}
