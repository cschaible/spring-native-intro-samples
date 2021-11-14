create sequence hibernate_sequence start 1 increment 1;

    create table todo_list (
       id int8 not null,
        identifier varchar(255) not null,
        primary key (id)
    );

    create table todolist_todos (
       todolist_id int8 not null,
        title varchar(255)
    );

    alter table if exists todo_list 
       add constraint UK_TodoList_Identifier unique (identifier);

    alter table if exists todolist_todos 
       add constraint FK_TodoList_Todo_TodoListId 
       foreign key (todolist_id) 
       references todo_list;

    create index IX_Todtod_Todist on todolist_todos (todolist_id);

