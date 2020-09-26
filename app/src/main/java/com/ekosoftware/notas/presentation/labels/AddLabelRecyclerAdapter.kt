package com.ekosoftware.notas.presentation.labels

import android.app.Activity
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.notas.R
import com.ekosoftware.notas.util.hideKeyboard
import com.ekosoftware.notas.util.showKeyboard
import kotlinx.android.synthetic.main.item_create_label.view.*

class AddLabelRecyclerAdapter(private val context: Context, private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLabelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_create_label, parent, false)
        return AddLabelViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AddLabelViewHolder).bindView()
    }

    inner class AddLabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private fun View.changeDrawables(hasFocus: Boolean) = if (hasFocus) {
            arrayOf(line_1, line_2).forEach { it.visibility = View.VISIBLE }
            btn_save.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddLabelRecyclerAdapter.context,
                    R.drawable.ic_check_24_accent
                )
            )
        } else {
            arrayOf(line_1, line_2).forEach { it.visibility = View.GONE }
            btn_save.setImageDrawable(null)
        }

        fun bindView() {

            itemView.txt_add_label.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    itemView.changeDrawables(hasFocus)
                    if (!hasFocus) {
                        this.setText("")
                        interaction?.focusLost()
                        this.error = null
                    }
                }
                setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.action == KeyEvent.ACTION_DOWN
                        && event.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        if (text.toString().alreadyExists()) {
                            error = this@AddLabelRecyclerAdapter.context.getString(R.string.name_already_exists)
                            (this@AddLabelRecyclerAdapter.context as Activity).showKeyboard()
                            false
                        } else insertNewLabel()
                        false
                    }
                    true
                }
            }

            itemView.btn_save.setOnClickListener {
                if (itemView.txt_add_label.text.toString().alreadyExists()) {
                    itemView.txt_add_label.requestFocus()
                    itemView.txt_add_label.error =
                        this@AddLabelRecyclerAdapter.context.getString(R.string.name_already_exists)
                } else {
                    itemView.txt_add_label.insertNewLabel()
                }
            }
        }

        private fun EditText.insertNewLabel() = this.apply {
            text.toString().takeIf { it.isNotEmpty() }?.let { input ->
                interaction?.onAddLabel(input)
            }
            setText("")
            clearFocus()
            focusSearch(View.FOCUS_UP)
        }
    }

    private var existingNamesList = listOf<String>()

    fun submitExistingNamesList(names: List<String>) {
        existingNamesList = names
    }

    private fun String.alreadyExists(): Boolean {
        for (name in existingNamesList) {
            if (name == this) return true
        }
        return false
    }

    interface Interaction {
        fun onAddLabel(name: String)
        fun focusLost()
    }
}