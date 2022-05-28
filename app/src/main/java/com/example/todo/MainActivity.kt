package com.example.todo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var todosListView: ListView

    private val todosList: ArrayList<TodoModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayTodos()
    }

    private fun displayTodos(){
        todosListView = findViewById(R.id.todo_listView)
        val todos: JSONArray = DB().getAll(this)

        for (i in 0 until todos.length()){
            val obj: JSONObject = todos.getJSONObject(i)
            val id: Long = obj.getLong("id")
            val title: String = obj.getString("title")
            val desc: String = obj.getString("desc")
            val date: String = obj.getString("date")
            val status: Boolean = obj.getBoolean("status")
            todosList.add(TodoModel(id, title, desc, date, status))
        }

        val emptyListView: View = layoutInflater.inflate(R.layout.empty_listview, null, false)
        addContentView(emptyListView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        todosListView.emptyView = emptyListView
        todosListView.adapter = TodoAdapter(this, R.layout.todo_card, todosList)
    }

    fun fabAddButtonAction(view: View) {
        TodoDialog(this,
            { title: String, desc: String, date: String ->
                val responseId: Long = DB().create(title, desc, date, view.context)
                if (responseId == 0L) {
                    return@TodoDialog false
                }
                todosList.add(TodoModel(responseId, title, desc, date, TodoModel().STATUS_PENDING))
                (todosListView.adapter as TodoAdapter).notifyDataSetChanged()
                return@TodoDialog true

            },
            "To Do Added",
            "Create",
            "Add"
            )
    }
}