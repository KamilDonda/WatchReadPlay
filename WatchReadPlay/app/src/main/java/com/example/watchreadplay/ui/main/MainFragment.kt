package com.example.watchreadplay.ui.main

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.watchreadplay.Data
import com.example.watchreadplay.DataAdapter
import com.example.watchreadplay.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_view.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var list: ArrayList<Data>

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

        radio_group_top.setOnCheckedChangeListener { _, checkedId ->
            val rb = view.findViewById(checkedId) as RadioButton

            val temp = ArrayList<Data>()
            list.forEach {
                if (rb.text == "All" || checkType(it.type!!, rb.text.toString()))
                    temp.add(it)
            }
            setupAdapter(temp)
        }

        radio_group_bottom.setOnCheckedChangeListener { _, checkedId ->
            val rb = view.findViewById(checkedId) as RadioButton

            val temp = ArrayList<Data>()
            list.forEach {
                if (rb.text == "All" || checkStatus(it, rb.text.toString()))
                    temp.add(it)
            }
            setupAdapter(temp)
        }

        fun showPopupMenu(v: View) {

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

        recycler_view.layoutManager = LinearLayoutManager(context)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()

                val user = auth.currentUser!!.uid

                for (row in snapshot.child(user).children) {
                    val newRow = row.getValue(Data::class.java)
                    newRow?.icon = setIcon(newRow?.type)
                    list.add(newRow!!)
                }
                preSetupAdapter(list)
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun addData() {
        val input = Data(
            "${Date().time}",
            "Serie",
            "The Falcon and the Winter Soldier",
            "The Falcon and the Winter Soldier",
            2021,
            "Marvel Cinematic Universe",
            "-",
            false
        )

        ref.child(auth.currentUser.uid).child(input.id).setValue(input)
    }

    private fun setupAdapter(list: ArrayList<Data>) {
        recycler_view.adapter = DataAdapter(list, ref, auth, requireContext())
    }

    private fun preSetupAdapter(list: ArrayList<Data>) {
        val temp = ArrayList<Data>()
        list.forEach {
            if (checkStatus(it, getString(R.string.finished)))
                temp.add(it)
        }
        setupAdapter(temp)
    }

    private fun checkType(type: String, text_radio: String): Boolean {
        return when (text_radio) {
            getString(R.string.movies) -> "Movie"
            getString(R.string.series) -> "Serie"
            getString(R.string.books) -> "Book"
            getString(R.string.games) -> "Game"
            else -> ""
        } == type
    }

    private fun checkStatus(item: Data, text_radio: String): Boolean {
        return (item.completion_date != "-" && text_radio == getString(R.string.finished) ||
                item.completion_date == "-" && text_radio == getString(R.string.wishlist))
    }

    private fun setIcon(type: String?): Drawable? {
        return when (type) {
            "Movie" -> getDrawable(requireContext(), R.drawable.ic_movie)
            "Serie" -> getDrawable(requireContext(), R.drawable.ic_serie)
            "Book" -> getDrawable(requireContext(), R.drawable.ic_book)
            "Game" -> getDrawable(requireContext(), R.drawable.ic_game)
            else -> null
        }
    }

    private fun showAddDialog() {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.dialog_view)
            .noAutoDismiss()

        dialog.findViewById<AppCompatImageButton>(R.id.type_button_dialog).setOnClickListener {
            val icon = dialog.findViewById<AppCompatImageView>(R.id.icon_dialog)
            val type = dialog.findViewById<MaterialTextView>(R.id.type_dialog)

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

        dialog.show()
    }
}