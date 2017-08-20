package com.rishiqing.demo

import grails.converters.JSON

class TodoController {
    def todoService

    def fetchTodoList() {
        def m
        try {
            List list = todoService.getList()
            m = [errcode: 0, result: list]
        }catch (Exception e){
            m = [errcode: 1]
        }
        render m as JSON
    }

    def saveTodoList() {

    }
}
