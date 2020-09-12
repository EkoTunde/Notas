package com.ekosoftware.notas.presentation.home

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.notas.R
import com.ekosoftware.notas.data.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NotesRecyclerViewAdapter(private val context: Context, private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private lateinit var touchHelper: ItemTouchHelper

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class NotesViewHolder(itemView: View, private val interaction: Interaction?) :
        RecyclerView.ViewHolder(itemView), View.OnTouchListener, GestureDetector.OnGestureListener {

        private val gestureDetector by lazy {
            GestureDetector(itemView.context, this@NotesViewHolder)
        }

        fun bind(item: Note) {
            itemView.apply {
                setOnTouchListener(this@NotesViewHolder)
                setOnClickListener {
                    interaction?.onItemSelected(item)
                }
                txt_title.text = item.title
                txt_content.text = item.content
            }
        }

        override fun onShowPress(p0: MotionEvent?) = Unit
        override fun onSingleTapUp(p0: MotionEvent?): Boolean = false // Return true if not working
        override fun onDown(p0: MotionEvent?): Boolean = false
        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = false // return true if not working
        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = false

        override fun onLongPress(p0: MotionEvent?) = touchHelper.startDrag(this@NotesViewHolder)

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            gestureDetector.onTouchEvent(event)
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> Unit
                    MotionEvent.ACTION_UP -> v?.performClick()
                    else -> Unit
                }
            }
            return true // True to consume event
        }
    }

    interface Interaction {
        fun onItemSelected(item: Note)
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NotesViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotesViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<Note>) {
        differ.submitList(list)
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        interaction?.onItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        interaction?.onDelete(position)
    }
}