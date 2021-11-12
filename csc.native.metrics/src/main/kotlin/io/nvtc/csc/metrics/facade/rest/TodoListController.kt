package io.nvtc.csc.metrics.facade.rest

import io.nvtc.csc.metrics.facade.rest.resource.UpdateTodoListResource
import io.nvtc.csc.metrics.model.Todo
import io.nvtc.csc.metrics.model.TodoList
import io.nvtc.csc.metrics.service.TodoListService
import java.util.*
import java.util.UUID.randomUUID
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoListController(private val todoListService: TodoListService) {

  @PostMapping("/todos/{todoListId}")
  fun addTodos(
      @PathVariable(name = "todoListId", required = false) todoListId: UUID?,
      @RequestBody todoListResource: UpdateTodoListResource
  ): TodoList =
      todoListService.update(
          TodoList(todoListId ?: randomUUID(), todoListResource.todos.map { Todo(it) }))
}
