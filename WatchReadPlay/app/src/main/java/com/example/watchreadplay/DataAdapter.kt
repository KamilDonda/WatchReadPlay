package com.example.watchreadplay

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

class DataAdapter(val list: ArrayList<Data>, val ref: DatabaseReference, val auth: FirebaseAuth) :
    RecyclerView.Adapter<DataAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row, parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val activity: MainActivity? = holder.itemView.context as MainActivity

        val root = holder.itemView.findViewById<CardView>(R.id.root)
        val description = holder.itemView.findViewById<LinearLayout>(R.id.description)

        val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
        val type = holder.itemView.findViewById<MaterialTextView>(R.id.type)
        val title = holder.itemView.findViewById<MaterialTextView>(R.id.title)
        val original_title = holder.itemView.findViewById<MaterialTextView>(R.id.original_title)
        val release_date = holder.itemView.findViewById<MaterialTextView>(R.id.release_date)
        val author = holder.itemView.findViewById<MaterialTextView>(R.id.author)
        val completion_date = holder.itemView.findViewById<MaterialTextView>(R.id.completion_date)
        val textViewList = listOf(
            title,
            original_title,
            release_date,
            author
        )

        // EditText
        val et_title = holder.itemView.findViewById<TextInputEditText>(R.id.title_et)
        val et_original_title =
            holder.itemView.findViewById<TextInputEditText>(R.id.original_title_et)
        val et_release_date = holder.itemView.findViewById<TextInputEditText>(R.id.release_date_et)
        val et_author = holder.itemView.findViewById<TextInputEditText>(R.id.author_et)
        val date_picker_button = holder.itemView.findViewById<Button>(R.id.date_picker_button)
        val save_button = holder.itemView.findViewById<Button>(R.id.save_button)
        val editTextList = listOf(
            et_title,
            et_original_title,
            et_release_date,
            et_author
        )

        val item = list[position]

        icon.setImageDrawable(item.icon)
        type.text = item.type
        title.text = item.title
        original_title.text = item.original_title
        release_date.text = item.release_date.toString()
        author.text = item.author
        completion_date.text = item.completion_date

        description.visibility = if (item.isClicked) View.VISIBLE else View.GONE
        date_picker_button.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE
        save_button.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE

        editTextList.zip(textViewList) { et, tv ->
            et.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE
            tv.visibility = if (item.isLongClicked) View.GONE else View.VISIBLE

            et.setText(tv.text)
        }

        root.setOnClickListener {
            if (!item.isLongClicked) {
                item.isClicked = !item.isClicked
                notifyItemChanged(position)
            }
        }

        root.setOnLongClickListener {
            if (item.isClicked) {
                item.isLongClicked = !item.isLongClicked
                notifyItemChanged(position)
            }
            true
        }

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        date_picker_button.setOnClickListener {
            if (activity != null) {
                datePicker.show(activity.supportFragmentManager, "TAG")
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("dd/MM/yyyy")
            return format.format(date)
        }

        datePicker.addOnPositiveButtonClickListener {
            completion_date.text = convertLongToTime(it)
        }

        fun updateItem() {
            val _id = item.id
            val _type = type.text.toString()
            val _title = et_title.text.toString()
            val _original_title = et_original_title.text.toString()
            val _release_date = et_release_date.text.toString().toInt()
            val _author = et_author.text.toString()
            val _completion_date = completion_date.text.toString()

            val newItem = Data(_id, _type, _title, _original_title, _release_date, _author, _completion_date)
            ref.child(auth.currentUser.uid).child(_id).setValue(newItem)
        }

        save_button.setOnClickListener {
            updateItem()
            item.isLongClicked = false
            notifyItemChanged(position)
        }
    }
}