package com.ekosoftware.notas.presentation.addedit

import android.os.Parcelable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import java.util.*

class ContentViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val undoList: MutableLiveData<LinkedList<Change>> = savedStateHandle.getLiveData("undoList", null)

    private fun addUndo(change: Change) {
        val currentStack = undoList.value ?: LinkedList()
        val newStack = currentStack.apply { push(change) }
        undoList.value = newStack
        redoList.value = LinkedList()
    }

    fun undo(): Change {
        val currentStack = undoList.value ?: LinkedList()
        val lastChange = currentStack.pop()
        undoList.value = currentStack
        addRedo(lastChange)
        return lastChange
    }

    private val redoList: MutableLiveData<LinkedList<Change>> = savedStateHandle.getLiveData("redoList", null)

    private fun addRedo(change: Change) {
        val currentStack = redoList.value ?: LinkedList()
        val newStack = currentStack.apply { push(change) }
        redoList.value = newStack
    }

    fun redo(): Change {
        val currentStack = redoList.value ?: LinkedList()
        val lastChange = currentStack.pop()
        redoList.value = currentStack
        addUndo(lastChange)
        return lastChange
    }

    private val clockRunning = MutableLiveData<Boolean>()

    fun startClock() {
        clockRunning.value = true
    }

    fun stopClock() {
        clockRunning.value = false
    }

    fun submitChange(change: Change) = change.takeIf { it != undoList.value?.peek() }?.let { addUndo(change) }

    val clock = clockRunning.distinctUntilChanged().switchMap {
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            flow {
                delay(2000)
                emit(true)
            }.collect {
                emit(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopClock()
    }
}

@Parcelize
data class Change(var text: String, var cursorPosition: Int) : Parcelable