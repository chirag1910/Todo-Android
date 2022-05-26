package com.example.todo

import android.R.attr.startYear
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.timerTask


public open class MainActivity : AppCompatActivity() {
    private var dialog: BottomSheetDialog? = null;

    private var titleField: EditText? = null;
    private var descField: EditText? = null;
    private var dateField: EditText? = null;

    private var clearButton: Button? = null;
    private var currentDate: Calendar = Calendar.getInstance();

    private var toast: TextView? = null;

    private val ANIMATION_DURATION: Long = 300;

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
        if (titleField?.text.toString() == ""){
            showToast("Title is required!");
        }else if (dateField?.text.toString() == ""){
            showToast("Date is required!")
        }else{
            if(!Todo().create(titleField!!.text.toString(), descField?.text?.toString(), dateField!!.text.toString(), this)){
                showToast("Some error occurred!")
            }else{
                clearButtonAction(view);
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
        Handler().postDelayed({ toast?.visibility = View.GONE }, ANIMATION_DURATION/2);
    }
}