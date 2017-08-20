package com.rishiqing.demo

class Todo {

    String title
    Boolean isDone
    Date doneTime
    Boolean isDeleted
    Date deletedTime

    Date dateCreated
    Date lastUpdated

    static mapping = {
        isDone defaultValue: false
        isDeleted defaultValue: false
    }

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
