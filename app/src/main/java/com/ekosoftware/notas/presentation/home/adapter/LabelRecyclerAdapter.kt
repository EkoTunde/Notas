package com.ekosoftware.notas.presentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.notas.R
import kotlinx.android.synthetic.main.item_title_label.view.*

class LabelRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_title_label, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as LabelViewHolder).bind(labelName)

    override fun getItemCount(): Int = 1

    inner class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(labelName: String) {
            itemView.txt_name.text = labelName
        }
    }

    private lateinit var labelName: String

    fun submitNewLabel(label: String?) {
        labelName = label ?: context.getString(R.string.all_notes_title)
        notifyDataSetChanged()
    }
}