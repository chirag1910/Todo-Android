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
import java.text.DateFormat
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private var dialog: BottomSheetDialog? = null;

    private var titleField: EditText? = null;
    private var descField: EditText? = null;
    private var dateField: EditText? = null;

    private var clearButton: Button? = null;
    private var currentDate: Calendar = Calendar.getInstance();

    private var toast: TextView? = null;

    private var todosListView: ListView? = null;

    private val ANIMATION_DURATION: Long = 300;

    private val todosList: MutableList<String> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayTodos();
    }

    private fun displayTodos(){
        todosListView = findViewById(R.id.todo_listView);
        val todos: JSONArray = Todo().getAll(this);

        for (i in 0 until todos.length()){
            val title: String = todos.getJSONObject(i).get("title").toString();
            todosList.add(title);
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            todosList);

        todosListView?.adapter = adapter;
    }

    private fun updateTodos(title: String){
        todosList.add(title);

        todosListView?.adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            todosList);
    }

    private fun setListeners (){
        titleField?.addTextChangedListener { setClearButtonVisibility() }
        descField?.addTextChangedListener { setClearButtonVisibility() }
        dateField?.addTextChangedListener { setClearButtonVisibility() }
    }

    fun addButtonAction(view: View) {
        dialog = BottomSheetDialog(view.context)
        dialog?.setContentView(layoutInflater.inflate(R.layout.add_dialog, null))
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED;
        dialog?.show()



        titleField = dialog?.findViewById(R.id.titleInput);
        descField = dialog?.findViewById(R.id.descInput);
        dateField = dialog?.findViewById(R.id.dateInput);
        clearButton = dialog?.findViewById(R.id.clearButton);
        toast = dialog?.findViewById(R.id.toast_add_dialog);

        setListeners();
    }

    fun hideDialog(view: View) {
        dialog?.hide()
    }

    fun showDatePicker(view: View){
        val datePickerDialog = DatePickerDialog(
            view.context,
            { datePicker, i, i2, i3 -> setDate(datePicker, i, i2, i3) },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis;
        datePickerDialog.show();
    }

    private fun setDate(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val newDate: Calendar = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        dateField?.setText(DateFormat.getDateInstance(DateFormat.LONG).format(newDate.time));
    }


    private fun setClearButtonVisibility(){
        hideToast();

        if (titleField?.text.toString() == "" && descField?.text.toString() == "" && dateField?.text.toString() == ""){
            clearButton?.alpha = 0F;
            clearButton?.isClickable = false;
            clearButton?.isFocusable = false;
        }else{
            clearButton?.isClickable = true;
            clearButton?.isFocusable = true;
            clearButton?.alpha = 1F;
        }
    }

    fun clearButtonAction(view: View){
        titleField?.text?.clear();
        descField?.text?.clear();
        dateField?.text?.clear();
    }

    fun submitButtonAction(view: View){
        val title: String = titleField!!.text.toString();
        val desc: String = descField!!.text.toString();
        val date: String = dateField!!.text.toString();

        if (title == ""){
            showToast("Title is required!");
        }else if (date == ""){
            showToast("Date is required!")
        }else{
            if(!Todo().create(title, desc, date, this)){
                showToast("Some error occurred!")
            }else{
                clearButtonAction(view);
                updateTodos(title);
                dialog?.hide();
            }
        }
    }

    private fun showToast(msg: String){
        toast?.text = msg;
        toast?.visibility = View.VISIBLE;
        toast?.animate()?.scaleY(1F)?.duration = ANIMATION_DURATION;
    }

    private fun hideToast(){
        toast?.animate()?.scaleY(0F)?.duration = ANIMATION_DURATION;
        Handler(Looper.getMainLooper()).postDelayed({ toast?.visibility = View.GONE }, ANIMATION_DURATION/2);
    }
}