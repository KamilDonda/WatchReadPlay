package com.example.watchreadplay.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchreadplay.Data
import com.example.watchreadplay.DataAdapter
import com.example.watchreadplay.MainActivity
import com.example.watchreadplay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var list: ArrayList<Data>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val firebase = FirebaseDatabase.getInstance(getString(R.string.firebase_database_url))
        ref = firebase.getReference("ArrayData")
        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_group_bottom.setOnCheckedChangeListener { group, checkedId ->
            val rb = view.findViewById(checkedId) as RadioButton
        }

        add_button.setOnClickListener {
            addData()
        }

        recycler_view.layoutManager = LinearLayoutManager(context)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()

                val user = auth.currentUser.uid

                for (row in snapshot.child(user).children) {
                    val newRow = row.getValue(Data::class.java)
                    list.add(newRow!!)
                }
                setupAdapter(list)
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun addData() {
        val input = Data("Serie",
            "The Falcon and the Winter Soldier",
            "The Falcon and the Winter Soldier",
            2021,
            "Marvel Cinematic Universe",
            "-",
            false
        )

//        ref.child(auth.currentUser.uid).child("${Date().time}").setValue(input)
    }

    private fun setupAdapter(list: ArrayList<Data>) {
        recycler_view.adapter = DataAdapter(list)
    }
}