package com.ekosoftware.notas.presentation.labels


import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean = oldItem == newItem
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
                    if (!hasFocus) { // When edit text lose focus
                        this.text.toString().takeIf { it != item.name }?.let { input -> // If it's text has changed
                            val newLabel = Label(item.id, input)    // Create label with changes
                            interaction?.onUpdateItem(newLabel, absoluteAdapterPosition)    //Update it in database
                        }
                        interaction?.focusLost()
                    }
                }
                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.action == KeyEvent.ACTION_DOWN
                        && event.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        v.clearFocus()
                        v.focusSearch(View.FOCUS_UP)
                        true
                    }
                    false
                }
            }

            itemView.btn_edit_save.setOnClickListener {
                if (!itemView.txt_add_label.hasFocus()) itemView.txt_add_label.requestFocus()
                else {
                    // Clear focus to trigger the listener
                    itemView.txt_add_label.clearFocus()
                    itemView.txt_add_label.focusSearch(View.FOCUS_UP)

                    // To hide keyboard
                    interaction?.focusLost()
                }
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
            btn_label_delete.setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.ic_outline_label_24))
            btn_edit_save.setImageDrawable(ContextCompat.getDrawable(adapterContext, R.drawable.ic_outline_edit_24))
        }
    }

    interface Interaction {
        fun onUpdateItem(label: Label, position: Int)
        fun onDeleteItem(label: Label)
        fun focusLost()
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

    fun submitList(list: List<Label>) = differ.submitList(list)

    fun notifyAddLine(list: List<Label>) {
        submitList(list)
        //notifyItemChanged(0) // Notify adapter "From item" has changed
        notifyItemInserted(0) // Notify adapter of new item
        //notifyDataSetChanged()
        //notifyItemRangeChanged(0, differ.currentList.size - 1)
    }

    fun deleteLabel(label: Label) {

    }
}