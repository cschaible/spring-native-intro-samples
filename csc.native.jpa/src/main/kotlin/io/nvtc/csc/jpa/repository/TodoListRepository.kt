package io.nvtc.csc.jpa.repository

import io.nvtc.csc.jpa.model.TodoList
import java.util.*
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface TodoListRepository : JpaRepository<TodoList, Long> {

  @EntityGraph(value = "TodoList.withDetails") fun findByIdentifier(identifier: UUID): TodoList?
}
