package com.rishiqing.demo.dao

import com.rishiqing.demo.Todo
import grails.transaction.Transactional

@Transactional
class TodoDaoService {

    Todo saveOrUpdate(Todo todo) {
        todo.save(failOnError: true)
        return todo
    }

    Todo remove(Todo todo){
        todo.isDeleted = true
        todo.save(failOnError: true)
        return todo
    }

    List getTodoList(){
        return Todo.findAllByIsDeleted(false)
    }

    Todo getTodoById(long id){
        return Todo.findByIdAndIsDeleted(id, false)
    }

    Todo findTodoListWhere(Map params){
        return Todo.findWhere(params)
    }

    List findAllTodoListWhere(Map params){
        return Todo.findAllWhere(params)
    }
}
