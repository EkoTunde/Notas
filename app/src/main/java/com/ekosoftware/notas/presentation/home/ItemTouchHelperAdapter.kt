package com.ekosoftware.notas.presentation.home

interface ItemTouchHelperAdapter {

    fun onItemMoved(fromPosition: Int, toPosition: Int)
    fun onItemSwiped(position: Int)

}