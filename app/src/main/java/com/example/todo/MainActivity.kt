package com.example.todo

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    var dialog: BottomSheetDialog? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun addButtonAction(view: View) {
        dialog = BottomSheetDialog(view.context)
        dialog!!.setContentView(layoutInflater.inflate(R.layout.fragment_add, null))
        dialog!!.show()
    }

    // wht to do fcking shit
    //zip this folder and send to me
    // mehh. will just upload to github..wait


    fun hideDialog(view: View) {
        Log.d("error", "Why the fck not updating")
        dialog!!.hide()
    }
}