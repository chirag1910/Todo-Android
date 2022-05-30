package com.example.todo

import android.database.DataSetObserver
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var todosListView: ListView
    private lateinit var appBarLayoutView: AppBarLayout
    private lateinit var fabShowDialogMini: FloatingActionButton

    private val todosList: ArrayList<TodoModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todosListView = findViewById(R.id.todo_listView)
        appBarLayoutView = findViewById(R.id.appBarLayout)
        fabShowDialogMini = findViewById(R.id.fab_show_create_dialog_mini)
        displayTodos()

        appBarLayoutView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _: AppBarLayout, verticalOffset: Int ->
            if (verticalOffset < -200) {
                fabShowDialogMini.visibility = View.GONE
            }
            else
            {
                fabShowDialogMini.visibility = View.VISIBLE
            }
        })
    }

    private fun sortTodos(someList : ArrayList<TodoModel>){
        for(k in 0 until someList.size-1){
            for (l in 0 until someList.size - 1 - k){
                if (someList[l].status == someList[l+1].status){
                    if (Date(someList[l].date) > Date(someList[l+1].date)){
                        someList[l] = someList[l+1].also { someList[l+1] = someList[l] }
                    }
                }else{
                    if (someList[l+1].status == TodoModel().STATUS_PENDING){
                        someList[l] = someList[l+1].also { someList[l+1] = someList[l] }
                    }
                }
            }
        }
    }

    private fun displayTodos(){
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
        if (todosList.size == 0)
            findViewById<TextView>(R.id.empty_listview).visibility = View.VISIBLE
        else
            findViewById<TextView>(R.id.empty_listview).visibility = View.GONE
        sortTodos(todosList)


        val adapter = TodoAdapter(this, R.layout.todo_card, todosList)
        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                if (todosList.size == 0)
                    findViewById<TextView>(R.id.empty_listview).visibility = View.VISIBLE
                else
                    findViewById<TextView>(R.id.empty_listview).visibility = View.GONE

                sortTodos(todosList)
            }
        })
        todosListView.adapter = adapter
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
