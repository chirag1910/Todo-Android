package com.example.todo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

class DB{
    private val SP_NAME = "todos.json";
    private val MACROS_FILE = "todo_macros";
    private val ID_KEY = "id";

    private fun getNextId(context: Context): Long{
        return context.getSharedPreferences(MACROS_FILE, Context.MODE_PRIVATE).getLong(ID_KEY, 0) + 1;
    }

    private fun incrementId(context: Context){
        val id: Long = context.getSharedPreferences(MACROS_FILE, Context.MODE_PRIVATE).getLong(ID_KEY, 0);
        context.getSharedPreferences(MACROS_FILE, Context.MODE_PRIVATE).edit().putLong(ID_KEY, id+1).apply();
    }

    private fun getFileContent(context: Context): String? {
        val file: File = File(context.filesDir.absolutePath, SP_NAME);
        if (file.exists()) {
            val reader: BufferedReader = file.bufferedReader()
            val content: String = reader.readText();
            reader.close()
            return content;
        }
        return null;
    }
    private fun saveFileContent(context: Context, content: String) : Boolean {
        val file: File = File(context.filesDir.absolutePath, SP_NAME);
        if (!file.exists())
            if (!file.createNewFile())
                return false;

        val writer: BufferedWriter = file.bufferedWriter()
        writer.write(content);
        writer.close()
        return true;
    }
    fun create(title: String, desc: String?, date: String, context: Context): Long{
        try{
            val todo: JSONObject = JSONObject();
            val nextId: Long = getNextId(context);
            todo.put("id", nextId);
            todo.put("title", title);
            todo.put("desc", desc);
            todo.put("date", date);

            val data: String? = getFileContent(context);
            val todos: JSONArray = if (data == null){
                JSONArray();
            }else{
                JSONArray(data);
            }

            todos.put(todo);
            incrementId(context);
            return if (saveFileContent(context, todos.toString())){
                nextId;
            }else{
                0;
            }

        }catch (error: Error){
            return 0;
        }
    }

    fun getAll(context: Context): JSONArray{
        val todos: String? = getFileContent(context);

        return if (todos == null){
            JSONArray();
        }else{
            JSONArray(todos);
        }
    }

    fun delete(context: Context, id: Long): Boolean{
        val todos: String? = getFileContent(context);
        if (todos == null){
            return false;
        }else{
            val todos = JSONArray(todos);

            for(i in 0 until todos.length()){
                if (todos.getJSONObject(i).getLong("id") == id){
                    todos.remove(i)
                    if (saveFileContent(context, todos.toString())){
                        return true
                    }
                }
            }

            return false
        }
    }
}