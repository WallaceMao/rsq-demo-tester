package com.rishiqing.demo

import grails.test.spock.IntegrationSpec
import grails.validation.ValidationException
import org.hibernate.SessionFactory
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Unroll

class TodoServiceIntegrationSpec extends IntegrationSpec {
    static transactional = true

    SessionFactory sessionFactory

    @Shared def todoDaoService
    @Shared def todoService

    @Shared Todo todo1
    @Shared Todo todo2
    @Shared Todo todo3
    @Shared Todo todo4
    @Shared Todo todo5
    @Shared Todo todo6

    def setupSpec(){
    }

    def cleanupSpec(){}

    def setup() {
        Date now = new Date()
        def (strTitle1, strTitle2, strTitle3, strTitle4, strTitle5, strTitle6) =
        ['aaa','bbb','ccc','ddd', 'eee', 'fff']

        todo1 = new Todo(title: strTitle1)
        todo1 = todoDaoService.saveOrUpdate(todo1)
        todo2 = new Todo(title: strTitle2, isDone: true, doneTime: now.getTime())
        todoDaoService.saveOrUpdate(todo2)
        todo3 = new Todo(title: strTitle3, isDeleted: true, deletedTime: now.getTime())
        todoDaoService.saveOrUpdate(todo3)
        todo4 = new Todo(title: strTitle4, isDone: true, doneTime: now.getTime(), isDeleted: true, deletedTime: now.getTime())
        todoDaoService.saveOrUpdate(todo4)
        todo5 = new Todo(title: strTitle5)
        todoDaoService.saveOrUpdate(todo5)
        todo6 = new Todo(title: strTitle6)
        todoDaoService.saveOrUpdate(todo6)

    }

    def cleanup() {}

    void "todoService.getTodoMapList get result map"() {
        when:
        List list = todoService.getTodoMapList()

        then:
        list.size() == 4  //isDeleted不会被读取
        todo1.toMap() == list[0]
        todo2.toMap() == list[1]
        todo5.toMap() == list[2]
        todo6.toMap() == list[3]
    }

    @Unroll
    void "todoService.saveList should NOT throw exception with params is #params"(){
        when:
        todoService.saveList(params)

        then:
        notThrown(Exception)

        where:
        params << [
                null,
                [],
                [1,2,3],  //元素不为Map
        ]
    }

    @Unroll
    void "todoService.saveList should throw exception #exception with params is #params"(){
        when:
        todoService.saveList(params)

        then:
        def e = thrown(exception)

        where:
        params << [
                [[title: 'asdf'],[aaa: 222]],  //元素的map中无id属性
                [[id: 99999, title: 'random99999']]  //元素的id在数据库中不存在
        ]
        exception << [
                ValidationException,
                RuntimeException
        ]
    }

    void "todoService.saveList should successfully save or update todo when params is valid"(){
        given:
        Map todo1Map = todo1.toMap()
        Map todo2Map = todo2.toMap()
        Map todo3Map = todo3.toMap()
        Map newTodo1 = [title: "newTodo1 ${new Date()}"]
        Map newTodo2 = [title: "newTodo2 ${new Date()}"]


        when:
        Map todo1MapChangeTitle = todo1Map + [title: "random${new Date()}"]
        Map todo2MapChangeIsDone = todo2Map + [isDone: !todo2Map.isDone]
        Map todo3MapChangeIsDeleted = todo3Map + [isDeleted: !todo3Map.isDeleted]

        def paramsList = [
                todo1MapChangeTitle,
                todo2MapChangeIsDone,
                todo3MapChangeIsDeleted,
                newTodo1,
                newTodo2
        ]
        List resultList = todoService.saveList(paramsList)
        Todo todo1 = todoDaoService.getTodoById(Long.valueOf(todo1MapChangeTitle.id))
        Todo todo2 = todoDaoService.getTodoById(Long.valueOf(todo2MapChangeIsDone.id))
        Todo todo3 = todoDaoService.getTodoById(Long.valueOf(todo3MapChangeIsDeleted.id))

        then: 'compare with database'
        todo1.id == todo1MapChangeTitle.id
        todo1.title == todo1MapChangeTitle.title
        todo2.id == todo2MapChangeIsDone.id
        todo2.isDone == todo2MapChangeIsDone.isDone
        todo3.id == todo3MapChangeIsDeleted.id
        todo3.isDone == todo3MapChangeIsDeleted.isDone

        and: 'check result order'
        resultList[0].id == paramsList[0].id
        resultList[1].id == paramsList[1].id
        resultList[2].id == paramsList[2].id
        resultList[3].id != null
        resultList[4].id != null

    }

    void "todoService.saveOrUpdateTodo should successfully save"(){
        when:
        String title = "new saved todo ${new Date()}"
        Map result = todoService.saveOrUpdateTodo([title: title])

        then:
        result.id != null
        result.title == title

        when:
        Todo todo = todoDaoService.getTodoById(result.id)

        then:
        todo.id == result.id
        todo.title == result.title
    }

    void "todoService.saveOrUpdateTodo should successfully update title"(){
        when:
        String titleChanged = "titleChanged${new Date()}"
        Map result = todoService.saveOrUpdateTodo([id: todo1.id, title: titleChanged])
        Todo todo = todoDaoService.getTodoById(todo1.id)

        then:
        result.id == todo1.id
        result.title == titleChanged
        todo.id == todo1.id
        todo.title == titleChanged
    }
    void "todoService.saveOrUpdateTodo should successfully update isDone"(){
        when:
        Date now = new Date()
        Map result = todoService.saveOrUpdateTodo([id: todo1.id, isDone: true, doneTime: now.getTime()])
        Todo todo = todoDaoService.getTodoById(todo1.id)

        then:
        result.id == todo1.id
        result.isDone == true
        result.doneTime == now.getTime()
        todo.id == todo1.id
        todo.isDone == true
        todo.doneTime == now.getTime()
    }

    void "todoService.removeTodo should successfully remove"(){
        when:
        Map result = todoService.removeTodo([id: todo5.id])
        Todo deletedTodo = todoDaoService.getTodoById(todo5.id)

        then:
        result.id == todo5.id
        deletedTodo.isDeleted == true
    }

    @Ignore
    void "todoService.saveList should successfully save result when params is #params"(){
        expect:
        1 == 2
    }
}
