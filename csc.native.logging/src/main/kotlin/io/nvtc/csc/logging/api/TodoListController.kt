package io.nvtc.csc.logging.api

import io.nvtc.csc.logging.api.resource.UpdateTodoListResource
import io.nvtc.csc.logging.model.Todo
import io.nvtc.csc.logging.model.TodoList
import io.nvtc.csc.logging.service.TodoListService
import java.util.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoListController(private val todoListService: TodoListService) {

  @GetMapping("/todos/{todoListId}")
  fun findTodoList(@PathVariable("todoListId") todoListId: UUID): TodoList? =
      todoListService.findByIdentifier(todoListId)

  @PostMapping("/todos/{todoListId}")
  fun addTodos(
      @PathVariable(name = "todoListId", required = false) todoListId: UUID?,
      @RequestBody todoListResource: UpdateTodoListResource
  ): TodoList =
      todoListId?.let { todoListService.findByIdentifier(it) }?.let { list ->
        list.todos = todoListResource.todos.map { Todo(it) }
        todoListService.update(list)
      }
          ?: todoListService.create(todoListId, todoListResource.todos.map { Todo(it) })
}
