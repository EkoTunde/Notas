package com.ekosoftware.notas.presentation.labels


import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.notas.R
import kotlinx.android.synthetic.main.item_create_label.view.*

class AddLabelRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<AddLabelRecyclerAdapter.AddLabelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_create_label, parent, false)
        return AddLabelViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: AddLabelViewHolder, position: Int) {
        holder.bindView()
    }

    inner class AddLabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView() {
            itemView.txt_add_label.apply {

                setEndIconOnClickListener {
                    this.clearFocus()
                    interaction?.onInsertItem(itemView.txt_add_label.editText?.text.toString())
                }

                editText?.setOnFocusChangeListener { _, hasFocus ->
                    editText?.setText("")
                    itemView.txt_add_label.isEndIconVisible = hasFocus
                }

                editText?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                        // If triggered by an enter key, this is the event; otherwise, this is null.
                        if (event != null) {
                            // if shift key is down, then we want to insert the '\n' char in the TextView;
                            // otherwise, the default action is to send the message.
                            if (!event.isShiftPressed) {
                                interaction?.onInsertItem(editText?.text.toString())
                                return true
                            }
                            return false
                        }
                        return true
                    }
                })
            }
        }
    }

    interface Interaction {
        fun onInsertItem(labelName: String)
    }
}