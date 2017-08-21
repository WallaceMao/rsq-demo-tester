package com.rishiqing.demo

class AppController {

    def index() {
    }

    def demotest(){
        Todo todo = Todo.findById(10L)
        todo.properties = [isDone: true]
        todo.save(failOnError: true, flush: true)
        render "------${new Date()}"
    }
}
