create sequence hibernate_sequence start with 1 increment by 1;

    create table todo_list (
       id bigint not null,
        identifier varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table todolist_todos (
       todolist_id bigint not null,
        title varchar(255)
    ) engine=InnoDB;

    alter table todo_list 
       add constraint UK_TodoList_Identifier unique (identifier);

    alter table todolist_todos 
       add constraint FK_TodoList_Todo_TodoListId 
       foreign key (todolist_id) 
       references todo_list (id);

    create index IX_Todtod_Todist on todolist_todos (todolist_id);

