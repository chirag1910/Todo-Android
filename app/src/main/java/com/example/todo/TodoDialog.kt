package com.example.todo

import android.app.DatePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DateFormat
import java.util.*

class TodoDialog(
    private var context: Context,
    private var submitButtonFunction: (String, String, String) -> Boolean,
    private var successMsg: String,
    private var heading: String,
    private var buttonText: String,
    private var titleValue: String = "",
    private var descValue: String = "",
    private var dateValue: String = ""
) {

    private var dialog: BottomSheetDialog = BottomSheetDialog(context);
    private var dialogHeading: TextView
    private var toast: TextView
    private var titleField: EditText
    private var descField: EditText
    private var dateField: EditText
    private var clearButton: Button
    private var submitButton: Button
    private var hideDialogButton: ImageButton;


    private val animationDuration: Long = 300
    private var currentDate: Calendar = Calendar.getInstance()

    init{
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.add_dialog, null))
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()

        dialogHeading = dialog.findViewById(R.id.dialog_heading)!!
        titleField = dialog.findViewById(R.id.titleInput)!!
        descField = dialog.findViewById(R.id.descInput)!!
        dateField = dialog.findViewById(R.id.dateInput)!!
        clearButton = dialog.findViewById(R.id.clearButtonBottomDialog)!!
        submitButton = dialog.findViewById(R.id.submitButtonBottomDialog)!!
        hideDialogButton = dialog.findViewById(R.id.hideButtonBottomDialog)!!
        toast = dialog.findViewById(R.id.toast_add_dialog)!!

        changeDialogTexts()
        setListeners()
        setClearButtonVisibility()
    }

    private fun changeDialogTexts() {
        titleField.setText(titleValue)
        descField.setText(descValue)
        dateField.setText(dateValue)

        dialogHeading.text = heading
        submitButton.text = buttonText
    }

    private fun setListeners (){
        submitButton.setOnClickListener{ submitButtonAction() }
        clearButton.setOnClickListener{ clearButtonAction() }
        dateField.setOnClickListener { showDatePicker() }
        hideDialogButton.setOnClickListener{ hideDialog() }

        titleField.addTextChangedListener { setClearButtonVisibility() }
        descField.addTextChangedListener { setClearButtonVisibility() }
        dateField.addTextChangedListener { setClearButtonVisibility() }
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

    private fun showToast(msg: String){
        toast.text = msg
        toast.visibility = View.VISIBLE
        toast.animate()?.scaleY(1F)?.duration = animationDuration
    }

    private fun hideToast(){
        toast.animate()?.scaleY(0F)?.duration = animationDuration
        Handler(Looper.getMainLooper()).postDelayed({ toast.visibility = View.GONE }, animationDuration/2)
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

    private fun showDatePicker(){
        val datePickerDialog = DatePickerDialog(
            context,
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

    private fun clearButtonAction(){
        titleField.text?.clear()
        descField.text?.clear()
        dateField.text?.clear()
    }

    private fun submitButtonAction(){
        val title: String = titleField.text.toString()
        val desc: String = descField.text.toString()
        val date: String = dateField.text.toString()

        if (title == ""){
            showToast("Title is required!")
        }else if (date == ""){
            showToast("Date is required!")
        }else{


            if(!submitButtonFunction(title, desc, date)){
                showToast("Some error occurred!")
            }else{
                dialog.dismiss()
                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
