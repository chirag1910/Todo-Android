package com.example.todo

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParsePosition
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var toast: TextView
    private lateinit var titleField: EditText
    private lateinit var descField: EditText
    private lateinit var dateField: EditText
    private lateinit var clearButton: Button

    private lateinit var todosListView: ListView

    private var currentDate: Calendar = Calendar.getInstance()
    private val ANIMATION_DURATION: Long = 300
    private val todosList: ArrayList<TodoModel> = ArrayList<TodoModel>()

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
            todosList.add(TodoModel(id, title, desc, date, TodoModel().STATUS_PENDING))
        }

        todosListView.adapter = TodoAdapter(this, R.layout.todo_card, todosList)
    }

    private fun updateTodos(id:Long, title: String, desc: String, date: String) {
        todosList.add(TodoModel(id, title, desc, date, TodoModel().STATUS_PENDING))
        todosListView.adapter = TodoAdapter(this, R.layout.todo_card, todosList)
    }

    private fun setListeners (){
        titleField.addTextChangedListener { setClearButtonVisibility() }
        descField.addTextChangedListener { setClearButtonVisibility() }
        dateField.addTextChangedListener { setClearButtonVisibility() }
    }

    fun addButtonAction(view: View) {
        dialog = BottomSheetDialog(view.context)
        dialog.setContentView(layoutInflater.inflate(R.layout.add_dialog, null))
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()



        titleField = dialog.findViewById(R.id.titleInput)!!
        descField = dialog.findViewById(R.id.descInput)!!
        dateField = dialog.findViewById(R.id.dateInput)!!
        clearButton = dialog.findViewById(R.id.clearButton)!!
        toast = dialog.findViewById(R.id.toast_add_dialog)!!

        setListeners()
    }

    fun hideDialog(view: View) {
        dialog.hide()
    }

    fun showDatePicker(view: View){
        val datePickerDialog = DatePickerDialog(
            view.context,
            { datePicker, i, i2, i3 -> setDate(datePicker, i, i2, i3) },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun setDate(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val newDate: Calendar = Calendar.getInstance()
        newDate.set(year, monthOfYear, dayOfMonth)
        dateField.setText(DateFormat.getDateInstance(DateFormat.LONG).format(newDate.time))
    }


    private fun setClearButtonVisibility(){
        hideToast()

        if (titleField.text.toString() == "" && descField.text.toString() == "" && dateField.text.toString() == ""){
            clearButton.alpha = 0F
            clearButton.isClickable = false
            clearButton.isFocusable = false
        }else{
            clearButton.isClickable = true
            clearButton.isFocusable = true
            clearButton.alpha = 1F
        }
    }

    fun clearButtonAction(view: View){
        titleField.text?.clear()
        descField.text?.clear()
        dateField.text?.clear()
    }

    fun submitButtonAction(view: View){
        val title: String = titleField.text.toString()
        val desc: String = descField.text.toString()
        val date: String = dateField.text.toString()

        if (title == ""){
            showToast("Title is required!")
        }else if (date == ""){
            showToast("Date is required!")
        }else{
            val responseId: Long = DB().create(title, desc, date, this)
            if(responseId == 0L){
                showToast("Some error occurred!")
            }else{
                updateTodos(responseId, title, desc, date)
                dialog.hide()
                Toast.makeText(this, "ToDo created", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteTodo(id: Long){
        for(i in 0 until todosList.size){
            todosList.forEach { item ->
                if (item.id == id){
                    if (DB().delete(this, id)){
                        todosList.remove(item)
                        todosListView.adapter = TodoAdapter(this, R.layout.todo_card, todosList)

                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
    }

    private fun showToast(msg: String){
        toast.text = msg
        toast.visibility = View.VISIBLE
        toast.animate()?.scaleY(1F)?.duration = ANIMATION_DURATION
    }

    private fun hideToast(){
        toast.animate()?.scaleY(0F)?.duration = ANIMATION_DURATION
        Handler(Looper.getMainLooper()).postDelayed({ toast.visibility = View.GONE }, ANIMATION_DURATION/2)
    }
}