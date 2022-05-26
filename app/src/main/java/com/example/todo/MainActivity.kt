package com.example.todo

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private var dialog: BottomSheetDialog? = null;
    private var titleField: EditText? = null;
    private var descField: EditText? = null;
    private var dateField: EditText? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun addButtonAction(view: View) {
        dialog = BottomSheetDialog(view.context)
        dialog!!.setContentView(layoutInflater.inflate(R.layout.fragment_add, null))
        dialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED;
        dialog!!.show()
    }

    fun hideDialog(view: View) {
        dialog!!.hide()
    }
}