package com.example.todo

// TODO: convert date type string to long

class TodoModel(){
    var id: Long = 0
    lateinit var title: String
    lateinit var desc: String
    lateinit var date: String
    var status: Boolean = false

    val STATUS_PENDING: Boolean = false
    val STATUS_COMPLETED: Boolean = true

    constructor(id: Long, title: String, desc: String, date: String, status: Boolean) : this() {
        this.id = id
        this.title = title
        this.desc = desc
        this.date = date
        this.status = status
    }

    override fun toString(): String {
        return date + "" + status.toString() + "\n"
    }
}
