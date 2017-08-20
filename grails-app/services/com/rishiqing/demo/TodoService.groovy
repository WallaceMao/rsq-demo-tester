package com.rishiqing.demo

import grails.transaction.Transactional

@Transactional
class TodoService {
    def todoDaoService

    List getTodoMapList() {
        todoDaoService.getTodoList()*.toMap()
    }

    void saveList(List mapList = []) {
        mapList.each { it ->
            if(!(it instanceof Map) || !it.id){
                return
            }
            Todo todo = todoDaoService.getTodoById(Long.valueOf(it.id))
            if(!todo){
                return
            }
            todo.properties = it
            if(todo.listDirtyPropertyNames().size() != 0){
                todoDaoService.saveOrUpdate(todo)
            }
        }
    }
}
