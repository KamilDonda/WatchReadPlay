package com.example.watchreadplay

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataAdapter(
    private val list: ArrayList<Data>,
    private val ref: DatabaseReference,
    private val auth: FirebaseAuth,
    private val context: Context
) :
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
        val menu = holder.itemView.findViewById<ConstraintLayout>(R.id.menu)

        val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
        val type = holder.itemView.findViewById<MaterialTextView>(R.id.type)
        val title = holder.itemView.findViewById<MaterialTextView>(R.id.title)
        val original_title = holder.itemView.findViewById<MaterialTextView>(R.id.original_title)
        val release_date = holder.itemView.findViewById<MaterialTextView>(R.id.release_date)
        val author = holder.itemView.findViewById<MaterialTextView>(R.id.author)
        val completion_date = holder.itemView.findViewById<MaterialTextView>(R.id.completion_date)
        val checkbox = holder.itemView.findViewById<CheckBox>(R.id.checkbox)
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
        val cancel_button = holder.itemView.findViewById<Button>(R.id.cancel_button)
        val delete_button = holder.itemView.findViewById<Button>(R.id.delete_button)
        val change_type_button = holder.itemView.findViewById<Button>(R.id.change_type_button)
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
        checkbox.isChecked = (item.completion_date != "-")

        description.visibility = if (item.isClicked) View.VISIBLE else View.GONE
        date_picker_button.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE
        menu.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE
        change_type_button.visibility = if (item.isLongClicked) View.VISIBLE else View.GONE
        checkbox.visibility = if (item.isLongClicked) View.GONE else View.VISIBLE

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
            val _release_date = et_release_date.text.toString()
            val _author = et_author.text.toString()
            val _completion_date = completion_date.text.toString()

            val newItem =
                Data(_id, _type, _title, _original_title, _release_date, _author, _completion_date)
            ref.child(auth.currentUser.uid).child(_id).setValue(newItem)
        }

        fun updateItem(bool: Boolean) {
            val date =
                if (bool) SimpleDateFormat("dd/MM/yyyy").format(Date())
                else "-"

            completion_date.text = date
            val newItem = item.copy(completion_date = date)
            ref.child(auth.currentUser.uid).child(newItem.id).setValue(newItem)
        }

        checkbox.setOnClickListener {
            updateItem(checkbox.isChecked)
        }

        save_button.setOnClickListener {
            updateItem()
            item.isLongClicked = false
            notifyItemChanged(position)
        }

        cancel_button.setOnClickListener {
            item.isLongClicked = false
            notifyItemChanged(position)
        }

        delete_button.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.title_delete))
                .setMessage(context.getString(R.string.supporting_text_delete))
                .setNeutralButton(context.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(context.getString(R.string.accept)) { _, _ ->
                    ref.child(auth.currentUser.uid).child(item.id).removeValue()
                    item.isLongClicked = false
                    notifyItemChanged(position)
                }
                .show()
        }

        // method to show popup menu
        fun showPopupMenu(v: View) {
            val popup = PopupMenu(context, v)

            popup.apply {
                // inflate the popup menu
                menuInflater.inflate(R.menu.popup_menu, popup.menu)
                // popup menu item click listener
                setOnMenuItemClickListener {
                    icon.setImageDrawable(it.icon)
                    type.text = it.title
                    false
                }
            }
            // show icons on popup menu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true)
            } else {
                try {
                    val fields = popup.javaClass.declaredFields
                    for (field in fields) {
                        if ("mPopup" == field.name) {
                            field.isAccessible = true
                            val menuPopupHelper = field[popup]
                            val classPopupHelper =
                                Class.forName(menuPopupHelper.javaClass.name)
                            val setForceIcons: Method = classPopupHelper.getMethod(
                                "setForceShowIcon",
                                Boolean::class.javaPrimitiveType
                            )
                            setForceIcons.invoke(menuPopupHelper, true)
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            popup.show()
        }

        change_type_button.setOnClickListener {
            showPopupMenu(it)
        }
    }
}