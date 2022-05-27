package com.example.todo

class TodoModel(){
    var id: Long = 0
    lateinit var title: String
    lateinit var desc: String
    lateinit var date: String
    var status: Byte = 0

    val STATUS_PENDING: Byte = 0
    val STATUS_COMPLETED: Byte = 1

    constructor(id: Long, title: String, desc: String, date: String, status: Byte) : this() {
        this.id = id
        this.title = title
        this.desc = desc
        this.date = date
        this.status = status
    }
}
