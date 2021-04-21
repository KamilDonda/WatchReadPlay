package com.example.watchreadplay.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var myAdapter: DataAdapter
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val firebase = FirebaseDatabase.getInstance(getString(R.string.firebase_database_url))
        ref = firebase.getReference("ArrayData")
        auth = FirebaseAuth.getInstance()

//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        myLayoutManager = LinearLayoutManager(context)

        val list = listOf(
            requireContext().getDrawable(R.drawable.ic_movie)?.let {
                Data("Movie",
                    "Władca Pierścieni: Powrót Króla",
                    "The Lord of the Rings: The Return of the King",
                    2003,
                    "Peter Jackson",
                    "15/04/2021 16:30",
                    false,
                    it
                )
            },
            requireContext().getDrawable(R.drawable.ic_movie)?.let {
                Data("Movie",
                    "Władca Pierścieni: Powrót Króla",
                    "The Lord of the Rings: The Return of the King",
                    2003,
                    "Peter Jackson",
                    "15/04/2021 16:30",
                    false,
                    it
                )
            },
            requireContext().getDrawable(R.drawable.ic_movie)?.let {
                Data("Movie",
                    "Władca Pierścieni: Powrót Króla",
                    "The Lord of the Rings: The Return of the King",
                    2003,
                    "Peter Jackson",
                    "15/04/2021 16:30",
                    false,
                    it
                )
            }
        )

        myAdapter = DataAdapter(list as List<Data>)

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_group_bottom.setOnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected
            val rb = view.findViewById(checkedId) as RadioButton
        }

        add_button.setOnClickListener {
            addData()
        }

        recyclerView = recycler_view.apply {
            layoutManager = myLayoutManager
            adapter = myAdapter
        }
    }

    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun addData() {
        val input = Data("Movie",
            "Władca Pierścieni: Powrót Króla",
            "The Lord of the Rings: The Return of the King",
            2003,
            "Peter Jackson",
            "15/04/2021 16:30",
            false
        )

        ref.child(auth.currentUser.uid).child("${Date().time}").setValue(input)
    }
}