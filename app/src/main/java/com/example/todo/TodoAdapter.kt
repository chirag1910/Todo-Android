package com.example.todo

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList

class TodoAdapter(context: Context, resource: Int, itemList: MutableList<TodoModel>) : ArrayAdapter<TodoModel>(context, resource, itemList) {

    private var mContext: Context = context;
    private var mItemList: MutableList<TodoModel> = itemList;
    private var mResource: Int = resource;

    private lateinit var cardView: LinearLayout;
    private lateinit var titleView: TextView;
    private lateinit var descView: TextView;
    private lateinit var dateView: TextView;
    private lateinit var sepView: View;
    private lateinit var dropdownIconView: ImageButton;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(mContext).inflate(mResource, parent, false);

        cardView = view.findViewById(R.id.todo_card);
        titleView = view.findViewById(R.id.todo_title);
        descView = view.findViewById(R.id.todo_desc);
        dateView = view.findViewById(R.id.todo_date);
        sepView = view.findViewById(R.id.todo_sep);
        dropdownIconView = view.findViewById(R.id.todo_dropdown_button);

        init(position);
        return view;
    }

    private fun init(position: Int){
        val dateString: String = mItemList[position].date;
        val desc: String = mItemList[position].desc;

        titleView.text = mItemList[position].title;
        dateView.text = dateString;

        if (desc.isNotEmpty()){
            descView.text = desc;
        }else{
            sepView.visibility = View.GONE;
            descView.visibility = View.GONE;
        }

        val date = Date(dateString);
        if (date > Calendar.getInstance().time){
            cardView.background.setColorFilter(Color.parseColor("#ccffcc"), PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(Color.parseColor("#198754"));
            dateView.setTextColor(Color.parseColor("#AA198754"));
            descView.setTextColor(Color.parseColor("#198754"));
            sepView.background.setColorFilter(Color.parseColor("#66ff66"), PorterDuff.Mode.SRC_ATOP);
        }else{
            cardView.background.setColorFilter(Color.parseColor("#ffcccc"), PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(Color.parseColor("#dc3545"));
            dateView.setTextColor(Color.parseColor("#AAdc3545"));
            descView.setTextColor(Color.parseColor("#dc3545"));
            sepView.background.setColorFilter(Color.parseColor("#ff6666"), PorterDuff.Mode.SRC_ATOP)
        }

        cardView.setOnLongClickListener {view: View ->
            val menu = PopupMenu(mContext, view);

            val EDIT_ACTION = "edit"
            val DELETE_ACTION = "delete"

            menu.menu.add(EDIT_ACTION).title = EDIT_ACTION
            menu.menu.add(DELETE_ACTION).title = DELETE_ACTION
            menu.show();

            menu.setOnMenuItemClickListener { item ->
                if (item.title == EDIT_ACTION){
                    val dialog= BottomSheetDialog(view.context)
                    dialog.setContentView(LayoutInflater.from(mContext).inflate(R.layout.add_dialog, null))
                    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    dialog.show()
                }else if (item.title == DELETE_ACTION){
                    if (DB().delete(mContext, mItemList[position].id)){
                        mItemList.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(mContext, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                return@setOnMenuItemClickListener true
            }

            return@setOnLongClickListener true;
        }
    }
}
