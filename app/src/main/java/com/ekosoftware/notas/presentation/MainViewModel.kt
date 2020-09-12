package com.ekosoftware.notas.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.domain.NoteRepository
import com.ekosoftware.notas.util.idsAsString
import com.ekosoftware.notas.util.sort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: NoteRepository
) : ViewModel() {

    private val sharedPref by lazy {
        appContext.getSharedPreferences(
            appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
    }

    fun saveIds(notes: MutableList<Note>) {
        val newSortedOrder = notes.idsAsString()
        with(sharedPref.edit()) {
            putString(appContext.getString(R.string.saved_notes_order), newSortedOrder)
            apply()
        }
    }

    private fun sortedIds() = sharedPref.getString(appContext.getString(R.string.saved_notes_order), "")

    val selectedNote = MediatorLiveData<Note>()

    fun selectNote(note: Note) {
        selectedNote.value = note
    }

    fun getAllNotes() = liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
        emit(Resource.Loading())
        try {
            emitSource(repository.getAllNotes().map {
                Resource.Success(
                    it.sort(sortedIds())
                )
            })
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }

    }

    fun insertNote(note: Note) = viewModelScope.launch {
        dispatchEvent(Event.INSERT)
        repository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        dispatchEvent(Event.UPDATE)
        repository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        dispatchEvent(Event.DELETE)
        repository.deleteNote(note)
    }

    val eventReceiver = MutableLiveData<Event>()

    private fun dispatchEvent(event: Event) {
        eventReceiver.value = event
    }
}

enum class Event {
    INSERT, UPDATE, DELETE
}