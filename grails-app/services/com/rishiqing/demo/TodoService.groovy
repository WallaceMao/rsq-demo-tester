package com.rishiqing.demo

import grails.transaction.Transactional

@Transactional
class TodoService {
    def todoDaoService

    List getTodoMapList() {
        todoDaoService.getTodoList()*.toMap()
    }

    Map saveOrUpdateTodo(Map params){
        Todo todo
        if(params.id){
            todo = todoDaoService.getTodoById(params.id)
            if(!todo){
                return new RuntimeException("todo with id ${params.id} not found")
            }
            todo.properties = params
        }else{
            todo = new Todo(params)
        }
        todo = todoDaoService.saveOrUpdate(todo)
        todo.toMap()
    }

    Map removeTodo(Map params){
        Todo todo = todoDaoService.getTodoById(params.id)
        todo = todoDaoService.remove(todo)
        todo.toMap()
    }

    /**
     * mapList包括了所有的todo，判断规则如下：
     * 1  todo.id存在，则执行更新操作
     * 2  todo.id不存在，则执行保存操作
     * 返回todo的map，map的顺序与参数mapList中的顺序一样
     * @param mapList
     * @return
     */
    List saveList(List mapList = []) {
        def resultList = []
        mapList.each { it ->
            if(!(it instanceof Map)){
                return
            }
            // 根据id是否存在，判断执行保存还是更新操作
            Todo todo
            if(it.id){
                todo = todoDaoService.getTodoById(Long.valueOf(it.id))
                if(!todo){
                    throw new RuntimeException("todo with id ${it.id} not found")
                }
                todo.properties = it
                if(todo.listDirtyPropertyNames().size() != 0){
                    todoDaoService.saveOrUpdate(todo)
                }
            }else{
                todo = todoDaoService.saveOrUpdate(new Todo(it))
                todoDaoService.saveOrUpdate(todo)
            }
            resultList.push(todo.toMap())
        }
        resultList
    }
}
