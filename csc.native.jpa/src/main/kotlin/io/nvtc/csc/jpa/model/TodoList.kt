package io.nvtc.csc.jpa.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.util.*
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.Table

@JsonIgnoreProperties("new")
@Entity
@NamedEntityGraph(name = "TodoList.withDetails", attributeNodes = [NamedAttributeNode("todos")])
@Table(
    indexes =
        [
            Index(name = "UK_TodoList_Identifier", columnList = "identifier", unique = true),
        ])
class TodoList(

    // Identifier
    @Column(nullable = false) @Type(type = "org.hibernate.type.UUIDCharType") var identifier: UUID,

    // List of todos
    @ElementCollection
    @CollectionTable(
        name = "todolist_todos",
        foreignKey = ForeignKey(name = "FK_TodoList_Todo_TodoListId"),
        joinColumns = [JoinColumn(name = "todolist_id")])
    var todos: List<Todo>
) : AbstractPersistable<Long>()
