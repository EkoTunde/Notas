package com.ekosoftware.notas.core

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class CustomEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTextContextMenuItem(id: Int): Boolean = when (id) {
        android.R.id.paste or android.R.id.cut -> {
            cutOrPasteListener?.onPaste()
            true
        }
        else -> super.onTextContextMenuItem(id)
    }

    private var cutOrPasteListener: CutOrPasteListener? = null

    fun setCustomTextListener(cutOrPasteListener: CutOrPasteListener) {
        this.cutOrPasteListener = cutOrPasteListener
    }

    interface CutOrPasteListener {
        fun onPaste()
    }

}