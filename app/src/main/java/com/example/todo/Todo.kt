package com.example.todo

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class Todo{
    private val SP_NAME = "TODOS";
    private val KEY = "REMINDERS";

    fun create(title: String, desc: String?, date: String, context: Context): Boolean{
        try{
            val todo: JSONObject = JSONObject();
            todo.put("title", title);
            todo.put("desc", desc);
            todo.put("date", date);

            val data: String? = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(KEY,"");
            var reminders: JSONArray = if (data == ""){
                JSONArray();
            }else{
                JSONArray(data);
            }

            println(reminders);

            reminders.put(todo);
            context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(KEY,reminders.toString()).apply();

            return true;
        }catch (error: Error){
            return false;
        }
    }
}