package com.example.watchreadplay.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchreadplay.Data
import com.example.watchreadplay.DataAdapter
import com.example.watchreadplay.R
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var myAdapter: DataAdapter
    private lateinit var myLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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

        recyclerView = recycler_view.apply {
            layoutManager = myLayoutManager
            adapter = myAdapter
        }
    }
}