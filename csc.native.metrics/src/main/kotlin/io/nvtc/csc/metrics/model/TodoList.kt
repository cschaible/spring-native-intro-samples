package io.nvtc.csc.metrics.model

import java.util.*

data class TodoList(

    // Identifier
    var identifier: UUID,

    // List of todos
    var todos: List<Todo>
)
