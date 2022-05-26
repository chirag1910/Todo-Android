package com.example.todo

import android.R.attr.startYear
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DateFormat
import java.time.LocalDate
import java.util.*


public open class MainActivity : AppCompatActivity() {
    private var dialog: BottomSheetDialog? = null;

    private var titleField: EditText? = null;
    private var descField: EditText? = null;
    private var dateField: EditText? = null;

    private var clearButton: Button? = null;
    private var currentDate: Calendar = Calendar.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private fun setListeners (){
        titleField?.addTextChangedListener { setClearButtonVisibility() }
        descField?.addTextChangedListener { setClearButtonVisibility() }
        dateField?.addTextChangedListener { setClearButtonVisibility() }
    }

    fun addButtonAction(view: View) {
        dialog = BottomSheetDialog(view.context)
        dialog?.setContentView(layoutInflater.inflate(R.layout.fragment_add, null))
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED;
        dialog?.show()

        titleField = dialog?.findViewById(R.id.titleInput);
        descField = dialog?.findViewById(R.id.descInput);
        dateField = dialog?.findViewById(R.id.dateInput);
        clearButton = dialog?.findViewById(R.id.clearButton);

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
}