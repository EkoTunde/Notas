package com.ekosoftware.notas.presentation

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.domain.NoteRepository
import com.ekosoftware.notas.util.idsAsString
import com.ekosoftware.notas.util.toLabelsList
import com.ekosoftware.notas.util.toLabelsPlainString
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: NoteRepository
) : ViewModel() {

    init {
        getNotesWithLabel(selectedLabel())
    }

    val selectedNote = MutableLiveData<Note>()

    fun selectNote(note: Note) {
        selectedNote.value = note
    }

    private val sharedPref by lazy {
        appContext.getSharedPreferences(
            appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
    }

    fun selectedLabel(): String? = sharedPref.getString(
        appContext.getString(R.string.saved_selected_list_title),
        appContext.getString(R.string.all_notes_title)
    )

    fun saveSelectedLabel(listTitle: String? = null) = with(sharedPref.edit()) {
        putString(appContext.getString(R.string.saved_selected_list_title), listTitle)
        apply()
    }

    fun saveIds(notes: MutableList<Note>) {
        val newSortedOrder = notes.idsAsString()
        with(sharedPref.edit()) {
            putString(appContext.getString(R.string.saved_notes_order), newSortedOrder)
            apply()
        }
    }

    private fun sortedIds() = sharedPref.getString(appContext.getString(R.string.saved_notes_order), "")

    fun getLabels() = sharedPref.getString(appContext.getString(R.string.saved_labels), "")?.toLabelsList() ?: listOf()

    fun List<Note>.saveLabels() = with(sharedPref.edit()) {
        putString(appContext.getString(R.string.saved_labels), this@saveLabels.toLabelsPlainString())
        apply()
    }

    private val selectedLabel = MutableLiveData<String>()

    fun getNotesWithLabel(label: String? = null) {
        this.selectedLabel.value = label
    }

    val notes = this.selectedLabel.distinctUntilChanged().switchMap { label ->
        liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(Resource.Loading())
            try {
                when (label) {
                    null -> emitSource(
                        repository.getAllNotes().map { result ->
                            result.saveLabels()
                            Resource.Success(result)
                        }
                    )
                    else -> emitSource(
                        repository.getNotesByLabel(label).map { Resource.Success(it) }
                    )
                }
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun searchNotes(search: String? = null) =
        liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.Default) {

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