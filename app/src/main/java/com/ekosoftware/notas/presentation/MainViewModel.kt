package com.ekosoftware.notas.presentation

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.domain.LabelRepository
import com.ekosoftware.notas.domain.NoteRepository
import com.ekosoftware.notas.util.toLabelsList
import com.ekosoftware.notas.util.toLabelsPlainString
import com.ekosoftware.notas.util.toPlainString
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    val currentSelectedLabel: MutableLiveData<String?> =
        savedStateHandle.getLiveData<String?>("selectedLabel", null)

    fun setLabel(label: String?) {
        currentSelectedLabel.value = label
    }

    val labels = liveData<Resource<List<Label>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
        emit(Resource.Loading())
        try {
            val result = labelRepository.getLabels()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    val selectedNote = MutableLiveData<Note>()

    fun selectNote(note: Note) {
        selectedNote.value = note
    }

    /*private fun sharedPref() =
        appContext.getSharedPreferences(appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    fun selectedLabel() = sharedPref().getString(
        appContext.getString(R.string.selected_label),
        appContext.getString(R.string.all_notes_title)
    )

    private fun saveSelectedLabel(label: String? = null) = with(sharedPref().edit()) {
        putString(appContext.getString(R.string.selected_label), label)
        apply()
    }

    fun labels() = sharedPref().getString(appContext.getString(R.string.saved_labels), "")?.toLabelsList() ?: listOf()

    fun addLabel(label: String) {
        labels().toMutableList().apply {
            add(label)
        }.also {
            it.saveLabels()
        }
    }

    private fun MutableList<String>.saveLabels() = with(sharedPref().edit()) {
        putString(appContext.getString(R.string.saved_labels), this@saveLabels.toPlainString())
        apply()
    }

    private fun List<Note>.saveLabelsFromNotes() = with(sharedPref().edit()) {
        putString(appContext.getString(R.string.saved_labels), this@saveLabelsFromNotes.toLabelsPlainString())
        apply()
    }

    private val selectedLabel = MutableLiveData<String?>()

    fun selectLabel(label: String? = null) {
        saveSelectedLabel(label)
        selectedLabel.value = appContext.getString(R.string.all_notes_title)
    }*/

    val notes = currentSelectedLabel.distinctUntilChanged().switchMap { label ->
        liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(Resource.Loading())
            try {
                when (label) {
                    null -> emitSource(
                        noteRepository.getAllNotes().map { result ->
                            Resource.Success(result)
                        }
                    )
                    else -> emitSource(
                        noteRepository.getNotesByLabel(label).map { Resource.Success(it) }
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
        noteRepository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        dispatchEvent(Event.UPDATE)
        noteRepository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        dispatchEvent(Event.DELETE)
        noteRepository.deleteNote(note)
    }

    fun addLabel(label: Label) = viewModelScope.launch {
        labelRepository.addLabel(label)
    }

    fun updateLabel(label: Label) = viewModelScope.launch {

    }

    val eventReceiver = MutableLiveData<Event>()

    private fun dispatchEvent(event: Event) {
        eventReceiver.value = event
    }
}

enum class Event {
    INSERT, UPDATE, DELETE
}