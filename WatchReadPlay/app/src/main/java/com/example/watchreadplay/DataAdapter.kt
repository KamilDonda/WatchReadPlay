package com.example.watchreadplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.row.view.*

class DataAdapter(val list: ArrayList<Data>) : RecyclerView.Adapter<DataAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row, parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val root = holder.itemView.findViewById<CardView>(R.id.root)
        val description = holder.itemView.findViewById<LinearLayout>(R.id.description)

        val icon = holder.itemView.findViewById<ImageView>(R.id.icon)
        val type = holder.itemView.findViewById<MaterialTextView>(R.id.type)
        val title = holder.itemView.findViewById<MaterialTextView>(R.id.title)
        val original_title = holder.itemView.findViewById<MaterialTextView>(R.id.original_title)
        val release_date = holder.itemView.findViewById<MaterialTextView>(R.id.release_date)
        val author = holder.itemView.findViewById<MaterialTextView>(R.id.author)
        val completion_date = holder.itemView.findViewById<MaterialTextView>(R.id.completion_date)

        val item = list[position]

        icon.setImageDrawable(item.icon)
        print(icon)
        type.text = item.type
        title.text = item.title
        original_title.text = item.original_title
        release_date.text = item.release_date.toString()
        author.text = item.author
        completion_date.text = item.completion_date

        description.visibility = if (item.isClicked) View.VISIBLE else View.GONE

        root.setOnClickListener {
            item.isClicked = !item.isClicked
            notifyItemChanged(position)
        }
    }
}