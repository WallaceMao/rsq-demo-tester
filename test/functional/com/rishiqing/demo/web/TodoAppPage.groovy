package com.rishiqing.demo.web

import geb.Page
import geb.navigator.Navigator
import org.openqa.selenium.Keys

/**
 * Created by  on 2017/8/21.Wallace
 */
class TodoAppPage extends Page {
    static url = "app#/all"
    static at = {
        title == "todo app"
        $('section.todoapp')
    }
    static content = {
        newInput(wait: true, required: true) { $('input.new-todo') }
        todoList() { $('ul.todo-list li') }
    }

    def createTodo(String str){
        newInput.click()
        newInput << str
        newInput << Keys.ENTER
    }
}
