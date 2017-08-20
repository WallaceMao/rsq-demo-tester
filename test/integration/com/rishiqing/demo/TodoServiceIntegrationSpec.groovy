package com.rishiqing.demo

import grails.test.spock.IntegrationSpec
import org.hibernate.SessionFactory
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Unroll

class TodoServiceIntegrationSpec extends IntegrationSpec {
    static transactional = true

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

    @Unroll
    void "todoService.getTodoMapList get result map is #todoMap, todo is #todo"() {
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
    void "todoService.saveList should not throw exception with params is #params"(){
        when:
        todoService.saveList(params)

        then:
        notThrown(Exception)

        where:
        params << [
                null,
                [],
                [1,2,3],  //元素不为Map
                [[title: 'asdf'],[aaa: 222]],  //元素的map中无id属性
                [[id: 99999, title: 'random99999']]  //元素的id在数据库中不存在
        ]
    }

    void "todoService.saveList should successfully save result when params is valid"(){
        given:
        Map todo1Map = todo1.toMap()
        Map todo2Map = todo2.toMap()
        Map todo3Map = todo3.toMap()


        when:
        Map todo1MapChangeTitle = todo1Map + [title: "random${new Date()}"]
        Map todo2MapChangeIsDone = todo2Map + [isDone: !todo2Map.isDone]
        Map todo3MapChangeIsDeleted = todo3Map + [isDeleted: !todo3Map.isDeleted]
        todoService.saveList([todo1MapChangeTitle, todo2MapChangeIsDone, todo3MapChangeIsDeleted])
        Todo todo1 = todoDaoService.getTodoById(Long.valueOf(todo1MapChangeTitle.id))
        Todo todo2 = todoDaoService.getTodoById(Long.valueOf(todo2MapChangeIsDone.id))
        Todo todo3 = todoDaoService.getTodoById(Long.valueOf(todo3MapChangeIsDeleted.id))

        then:
        todo1.id == todo1MapChangeTitle.id
        todo1.title == todo1MapChangeTitle.title
        todo2.id == todo2MapChangeIsDone.id
        todo2.isDone == todo2MapChangeIsDone.isDone
        todo3.id == todo3MapChangeIsDeleted.id
        todo3.isDone == todo3MapChangeIsDeleted.isDone

    }

    @Ignore
    void "todoService.saveList should successfully save result when params is #params"(){
        expect:
        1 == 2
    }
}
