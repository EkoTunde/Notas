package com.ekosoftware.notas.presentation.labels


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.notas.R
import com.ekosoftware.notas.data.model.Label
import kotlinx.android.synthetic.main.item_label.view.*

class LabelsRecyclerAdapter(private val context: Context, private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Label>() {

        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class LabelsViewHolder(
        itemView: View,
        private val adapterContext: Context,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Label) {
            itemView.txt_add_label.apply {
                setText(item.name)
                setOnFocusChangeListener { _, hasFocus ->
                    itemView.changeDrawables(hasFocus)
                    if (!hasFocus) interaction?.onUpdateItem(item)
                }
            }
            itemView.btn_edit_save.setOnClickListener {
                if (!itemView.txt_add_label.hasFocus()) itemView.txt_add_label.requestFocus()
                else interaction?.onUpdateItem(item)
            }

            itemView.btn_label_delete.setOnClickListener {
                if (!itemView.txt_add_label.hasFocus()) itemView.txt_add_label.requestFocus()
                else interaction?.onDeleteItem(item)
            }
        }

        private fun View.changeDrawables(hasFocus: Boolean) = if (hasFocus) {
            arrayOf(line_1, line_2).forEach { it.visibility = View.VISIBLE }
            btn_label_delete.setImageDrawable(
                ContextCompat.getDrawable(adapterContext, R.drawable.ic_outline_delete_24)
            )
            btn_edit_save.setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.ic_check_24_accent))
        } else {
            arrayOf(line_1, line_2).forEach { it.visibility = View.GONE }
            btn_label_delete.setImageDrawable(
                ContextCompat.getDrawable(adapterContext, R.drawable.ic_outline_label_24)
            )
            btn_edit_save.setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.ic_outline_edit_24))
        }
    }

    interface Interaction {
        fun onUpdateItem(label: Label)
        fun onDeleteItem(label: Label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false)
        return LabelsViewHolder(view, context, interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LabelsViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<Label>) {
        differ.submitList(list)
    }
}