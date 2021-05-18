package com.example.watchreadplay.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.watchreadplay.Config
import com.example.watchreadplay.Data
import com.example.watchreadplay.DataAdapter
import com.example.watchreadplay.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_search.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var list: ArrayList<Data>

    private lateinit var currentContext: Context

    private var config = Config()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firebase = FirebaseDatabase.getInstance(getString(R.string.firebase_database_url))
        ref = firebase.getReference("ArrayData")
        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_group_top.setOnCheckedChangeListener { radioGroup: RadioGroup, _ ->
            setupAdapter(view)
            setRadio(radioGroup, true)
        }

        radio_group_bottom.setOnCheckedChangeListener { radioGroup: RadioGroup, _ ->
            setupAdapter(view)
            setRadio(radioGroup, false)
        }

        add_button.setOnClickListener {
            showAddDialog()
        }

        logout_button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.title_logout))
                .setMessage(requireContext().getString(R.string.supporting_text_logout))
                .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(requireContext().getString(R.string.accept)) { _, _ ->
                    signOut()
                }
                .show()
        }

        search_button.setOnClickListener {
            showSearchDialog(view)
        }

        reset_button.setOnClickListener {
            setupAdapter(view)
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()

                val user = auth.currentUser?.uid

                if (user != null) {
                    val record = snapshot.child(user)

                    val c = record.child("config").getValue(Config::class.java) ?: Config()
                    config.apply {
                        top = c.top
                        bottom = c.bottom
                    }
                    selectRadios(view)

                    val items = record.child("items").children
                    for (row in items) {
                        val newRow = row.getValue(Data::class.java)
                        newRow?.icon = setIcon(newRow?.type)
                        list.add(newRow!!)
                    }
                    setupAdapter(view)
                }
            }
        })
    }

    private fun signOut() {
        ref.child(auth.currentUser.uid).child("config").setValue(config)
        auth.signOut()
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        requireActivity().recreate()
    }

    private fun selectRadios(view: View) {
        val radioGroup_top = view.findViewById<RadioGroup>(R.id.radio_group_top)
        val top = config.top
        (radioGroup_top.getChildAt(top) as RadioButton).isChecked = true

        val radioGroup_bottom = view.findViewById<RadioGroup>(R.id.radio_group_bottom)
        val bottom = config.bottom
        (radioGroup_bottom.getChildAt(bottom) as RadioButton).isChecked = true
    }

    private fun setRadio(radioGroup: RadioGroup, top: Boolean) {
        val id = radioGroup.checkedRadioButtonId
        val rb = radioGroup.findViewById<RadioButton>(id)
        val index = radioGroup.indexOfChild(rb)

        if (top) config.top = index
        else config.bottom = index
    }

    private fun setupAdapter(view: View, items: ArrayList<Data> = list) {
        val radioGroup_top = view.findViewById<RadioGroup>(R.id.radio_group_top)
        val checkedId_type: Int = radioGroup_top.checkedRadioButtonId
        val checkedType = view.findViewById(checkedId_type) as RadioButton
        val type = checkedType.text.toString()

        val radioGroup_bottom = view.findViewById<RadioGroup>(R.id.radio_group_bottom)
        val checkedId_status: Int = radioGroup_bottom.checkedRadioButtonId
        val checkedStatus = view.findViewById(checkedId_status) as RadioButton
        val status = checkedStatus.text.toString()

        if (items.isNotEmpty())
            view.findViewById<ConstraintLayout>(R.id.message).visibility = View.GONE

        val temp = ArrayList<Data>()
        items.forEach {
            if (type == get_string(R.string.all) || checkType(it.type!!, type))
                if (checkStatus(it, status))
                    temp.add(it)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = DataAdapter(temp, ref, auth, currentContext)
    }

    private fun checkType(type: String, text_radio: String): Boolean {
        return when (text_radio) {
            get_string(R.string.movies) -> "Movie"
            get_string(R.string.series) -> "Serie"
            get_string(R.string.books) -> "Book"
            get_string(R.string.games) -> "Game"
            else -> ""
        } == type
    }

    private fun checkStatus(item: Data, text_radio: String): Boolean {
        return (item.completion_date != "-" && text_radio == get_string(R.string.finished) ||
                item.completion_date == "-" && text_radio == get_string(R.string.wishlist) ||
                text_radio == get_string(R.string.all))
    }

    private fun setIcon(type: String?): Drawable? {
        return when (type) {
            "Movie" -> get_drawable(R.drawable.ic_movie)
            "Serie" -> get_drawable(R.drawable.ic_serie)
            "Book" -> get_drawable(R.drawable.ic_book)
            "Game" -> get_drawable(R.drawable.ic_game)
            else -> null
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showAddDialog() {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_view)
            .noAutoDismiss()

        val icon = dialog.findViewById<AppCompatImageView>(R.id.icon_dialog)
        val type = dialog.findViewById<MaterialTextView>(R.id.type_dialog)
        val title = dialog.findViewById<TextInputEditText>(R.id.title_dialog)
        val original_title = dialog.findViewById<TextInputEditText>(R.id.title_original_dialog)
        val author = dialog.findViewById<TextInputEditText>(R.id.author_dialog)
        val release_date = dialog.findViewById<TextInputEditText>(R.id.release_date_dialog)
        val completion_date = dialog.findViewById<MaterialTextView>(R.id.completion_date_dialog)
        val add_button = dialog.findViewById<Button>(R.id.add_button_dialog)
        val bottom_margin = dialog.findViewById<View>(R.id.bottom_margin_dialog)

        // When user is typing sth in the 'original title' field and the 'title' field is empty
        // then in the 'title' field the content of the 'original title' field appears
        var isTitleEmpty = true
        title.doOnTextChanged { _, _, _, _ ->
            if (title.isFocused)
                isTitleEmpty = false
            if (title.text.isNullOrEmpty())
                isTitleEmpty = true
        }
        original_title.doOnTextChanged { text, _, _, _ ->
            if (isTitleEmpty)
                title.setText(text)
        }

        // Select type
        dialog.findViewById<AppCompatImageButton>(R.id.type_button_dialog).setOnClickListener {

            val popup = PopupMenu(context, icon)

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

        // Set finish date
        dialog.findViewById<MaterialButton>(R.id.date_picker_button_dialog).setOnClickListener {
            @SuppressLint("SimpleDateFormat")
            fun convertLongToTime(time: Long): String {
                val date = Date(time)
                val format = SimpleDateFormat("dd/MM/yyyy")
                return format.format(date)
            }

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.show(requireActivity().supportFragmentManager, "TAG")

            datePicker.addOnPositiveButtonClickListener {
                completion_date.text = convertLongToTime(it)
            }
        }

        // Add data
        add_button.setOnClickListener {
            var compDate = completion_date.text.toString()
            if (compDate.isEmpty()) compDate = "-"

            if (title.text.isNullOrEmpty()) {
                it.hideKeyboard()
                bottom_margin.visibility = View.VISIBLE
                Snackbar.make(it, getString(R.string.title_is_empty), Snackbar.LENGTH_SHORT)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            bottom_margin.visibility = View.GONE
                        }
                    }).show()
            } else {
                val input = Data(
                    "${Date().time}",
                    type.text.toString(),
                    title.text.toString(),
                    original_title.text.toString(),
                    release_date.text.toString(),
                    author.text.toString(),
                    compDate
                )

                ref.child(auth.currentUser.uid).child("items").child(input.id).setValue(input)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showSearchDialog(view: View) {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_search)
            .noAutoDismiss()

        val search_field = dialog.findViewById<TextInputEditText>(R.id.search_field_dialog)
        val chipGroup = dialog.findViewById<ChipGroup>(R.id.chipGroup_dialog)
        val search_button = dialog.findViewById<MaterialButton>(R.id.search_button_dialog)
        val reset_button = dialog.findViewById<MaterialButton>(R.id.reset_button_dialog)
        val bottom_margin = dialog.findViewById<View>(R.id.bottom_margin_dialog_search)

        val radioGroup_top = view.findViewById<RadioGroup>(R.id.radio_group_top)

        reset_button.setOnClickListener {
            setupAdapter(view)
        }

        search_button.setOnClickListener {
            // Select radio button from top group
            val chip = chipGroup.children
                .toList()
                .first { (it as Chip).isChecked } as Chip

            val radio = radioGroup_top.children
                .toList()
                .first { (it as RadioButton).text == chip.text }

            radioGroup_top.check(radio.id)

            if (!search_field.text.isNullOrBlank()) {
                val title = search_field.text.toString().toLowerCase()
                val temp = ArrayList<Data>()
                list.forEach {
                    if (it.title.toLowerCase(Locale.ROOT)
                            .contains(title) || it.original_title.toLowerCase(
                            Locale.ROOT
                        ).contains(title)
                    )
                        temp.add(it)
                }
                dialog.dismiss()
                setupAdapter(view, temp)
            } else {
                bottom_margin.visibility = View.VISIBLE
                Snackbar.make(it, getString(R.string.title_is_empty_search), Snackbar.LENGTH_SHORT)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            bottom_margin.visibility = View.GONE
                        }
                    }).show()
            }
        }

        dialog.show()
    }

    private fun get_string(id: Int): String {
        return currentContext.getString(id)
    }

    private fun get_drawable(id: Int): Drawable? {
        return getDrawable(currentContext, id)
    }

    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null)
            ref.child(auth.currentUser.uid).child("config").setValue(config)
    }
}