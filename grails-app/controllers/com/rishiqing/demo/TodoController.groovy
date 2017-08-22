package com.rishiqing.demo

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class TodoController {
    def todoService

    def fetchTodoList() {
        def m
        try {
            List list = todoService.getTodoMapList()
            m = [errcode: 0, result: list]
        }catch (Exception e){
            log.error(e)
            m = [errcode: 1]
        }
        render m as JSON
    }

    def saveTodo(){
        def m
        try {
            def jsonSlurper = new JsonSlurper()
            JSONObject json = request.JSON
            Map params = (Map)jsonSlurper.parseText(json.toString())
            Map result = todoService.saveOrUpdateTodo(params)
            m = [errcode: 0, result: result]
        }catch (Exception e){
            log.error(e)
            m = [errcode: 1]
        }
        render m as JSON
    }

    def removeTodo(){
        def m
        try {
            def jsonSlurper = new JsonSlurper()
            JSONObject json = request.JSON
            Map params = (Map)jsonSlurper.parseText(json.toString())
            Map result = todoService.removeTodo(params)
            m = [errcode: 0, result: result]
        }catch (Exception e){
            log.error(e)
            m = [errcode: 1]
        }
        render m as JSON
    }
}
