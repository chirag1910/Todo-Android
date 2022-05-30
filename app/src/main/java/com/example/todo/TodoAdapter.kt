package com.example.todo

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import java.util.Date

class TodoAdapter(context: Context, resource: Int, itemList: MutableList<TodoModel>) : ArrayAdapter<TodoModel>(context, resource, itemList) {

    private var mContext: Context = context
    private var mItemList: MutableList<TodoModel> = itemList
    private var mResource: Int = resource

    private lateinit var cardView: LinearLayout
    private lateinit var titleView: TextView
    private lateinit var descView: TextView
    private lateinit var dateView: TextView
    private lateinit var sepView: View
    private lateinit var dropdownIconView: ImageButton

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(mContext).inflate(mResource, parent, false)

        cardView = view.findViewById(R.id.todo_card)
        titleView = view.findViewById(R.id.todo_title)
        descView = view.findViewById(R.id.todo_desc)
        dateView = view.findViewById(R.id.todo_date)
        sepView = view.findViewById(R.id.todo_sep)
        dropdownIconView = view.findViewById(R.id.todo_dropdown_button)

        init(position)
        return view
    }

    private fun init(position: Int){
        val titleString: String = mItemList[position].title
        val dateString: String = mItemList[position].date
        val descString: String = mItemList[position].desc
        val statusBool: Boolean = mItemList[position].status

        titleView.text = titleString
        dateView.text = dateString

        if (descString.isNotEmpty()){
            descView.text = descString
        }else{
            sepView.visibility = View.GONE
            descView.visibility = View.GONE
        }

        if (mItemList[position].status == TodoModel().STATUS_COMPLETED){
            cardView.background.setColorFilter(Color.parseColor("#ccffcc"), PorterDuff.Mode.SRC_ATOP)
            titleView.setTextColor(Color.parseColor("#198754"))
            dateView.setTextColor(Color.parseColor("#AA198754"))
            descView.setTextColor(Color.parseColor("#198754"))
            sepView.background.setColorFilter(Color.parseColor("#AA198754"), PorterDuff.Mode.SRC)
        }else{
            val date = Date(dateString)
            val currTime = Date()
            if (date.date + date.month*100 + date.year*10000 >= currTime.date + currTime.month*100 + currTime.year*10000){
//                Log.e("Current time", date.time.toString() + " " + (currTime - (currTime % 100000)).toString())
                cardView.background.setColorFilter(Color.parseColor("#ccccff"), PorterDuff.Mode.SRC_ATOP)
                titleView.setTextColor(Color.parseColor("#0d6efd"))
                dateView.setTextColor(Color.parseColor("#AA0d6efd"))
                descView.setTextColor(Color.parseColor("#0d6efd"))
                sepView.background.setColorFilter(Color.parseColor("#AA0d6efd"), PorterDuff.Mode.SRC)
            }else{
                cardView.background.setColorFilter(Color.parseColor("#ffcccc"), PorterDuff.Mode.SRC_ATOP)
                titleView.setTextColor(Color.parseColor("#dc3545"))
                dateView.setTextColor(Color.parseColor("#AAdc3545"))
                descView.setTextColor(Color.parseColor("#dc3545"))
                sepView.background.setColorFilter(Color.parseColor("#AAdc3545"), PorterDuff.Mode.SRC)
            }
        }

        dropdownIconView.setOnClickListener {view: View -> showMenu(view, position, titleString, descString, dateString, statusBool); }
        cardView.setOnLongClickListener {view: View ->
            showMenu(view, position, titleString, descString, dateString, statusBool)
            return@setOnLongClickListener true
        }


    }

    private fun showMenu(view: View, position: Int, titleString: String, descString: String, dateString: String, statusBool: Boolean){
        val EDIT_ACTION = "Edit"
        val DELETE_ACTION = "Delete"
        val STATUS_ACTION = if (statusBool == TodoModel().STATUS_PENDING){
            "Set As Done"
        }else{
            "Set As Pending"
        }

        val menu = PopupMenu(mContext, view)
        menu.menu.add(EDIT_ACTION).title = EDIT_ACTION
        menu.menu.add(DELETE_ACTION).title = DELETE_ACTION
        menu.menu.add(STATUS_ACTION).title = STATUS_ACTION
        menu.show()

        menu.setOnMenuItemClickListener { item ->
            if (item.title == EDIT_ACTION){
                TodoDialog(
                    mContext,
                    { title: String, desc: String, date: String ->
                        if(DB().update(mContext, mItemList[position].id, title, desc, date)){
                            mItemList[position] = TodoModel(mItemList[position].id, title, desc, date, TodoModel().STATUS_PENDING)
                            notifyDataSetChanged()
                            return@TodoDialog true
                        }
                        return@TodoDialog false
                    },
                    "To Do Updated",
                    "Edit",
                    "Update",
                    titleString,
                    descString,
                    dateString
                )
            }else if (item.title == DELETE_ACTION){
                if (DB().delete(mContext, mItemList[position].id)){
                    mItemList.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(mContext, "To Do Deleted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(mContext, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }else if(item.title == STATUS_ACTION){
                val newStatus = !statusBool

                if(DB().updateStatus(mContext, mItemList[position].id, newStatus)){
                    mItemList[position].status = newStatus
                    notifyDataSetChanged()
                    Toast.makeText(mContext, "Status Updated", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(mContext, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

}
