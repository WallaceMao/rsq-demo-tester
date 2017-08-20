package com.rishiqing.demo

class Todo {

    String title
    Boolean isDone = false
    Date doneTime
    Boolean isDeleted = false
    Date deletedTime

    Date dateCreated
    Date lastUpdated

    static constraints = {
        doneTime nullable: true
        deletedTime nullable: true
    }

    Map toMap(){
        return [
                title: title,
                isDone: isDone,
                doneTime: doneTime?.getTime(),
                isDeleted: isDeleted,
                deletedTime: deletedTime?.getTime()
        ]
    }
}
