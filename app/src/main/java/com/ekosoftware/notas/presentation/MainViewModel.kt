package com.ekosoftware.notas.presentation

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.domain.LabelRepository
import com.ekosoftware.notas.domain.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val ARG_SELECTED_LABEL_ID = "selectedLabelId"
        private const val ARG_SELECTED_LABEL_NAME = "selectedLabelName"
        private const val TAG = "MainViewModel"
    }

    val currentSelectedLabelId: MutableLiveData<Long?> =
        savedStateHandle.getLiveData<Long?>("selectedLabelId", getSavedLabelId())

    private fun getSavedLabelId(): Long? = sharedPref().getLong(ARG_SELECTED_LABEL_ID, -1).let {
        // Saving null values in SharedPreferences is not allowed for Long, so -1 is representing null's
        return if (it == -1L) null else it
    }

    val currentSelectedLabelName: MutableLiveData<String?> =
        savedStateHandle.getLiveData("selectedLabelName", sharedPref().getString(ARG_SELECTED_LABEL_NAME, null))

    fun selectLabel(label: Label?) {
        currentSelectedLabelId.value = label?.id
        currentSelectedLabelName.value = label?.name

        with(sharedPref().edit()) {
            Log.d(TAG, "saving: label is ${currentSelectedLabelName.value} with id ${currentSelectedLabelId.value}")
            putLong(ARG_SELECTED_LABEL_ID, label?.id ?: -1)
            putString(ARG_SELECTED_LABEL_NAME, currentSelectedLabelName.value)
            apply()
        }
    }

    fun selectedLabel() = Label(currentSelectedLabelId.value, currentSelectedLabelName.value)

    val labels = liveData<Resource<List<Label>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emitSource(labelRepository.getLabels().map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    private fun sharedPref() = appContext.getSharedPreferences(
        appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    val selectedNote: MutableLiveData<Note?> = savedStateHandle.getLiveData<Note?>("selectedNote", null)

    fun selectNote(note: Note?) {
        selectedNote.value = note
    }

    val notes = currentSelectedLabelId.distinctUntilChanged().switchMap { labelId ->
        Log.d(TAG, "notes...: label id is $labelId")
        liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                emitSource(noteRepository.getNotesByLabel(labelId).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    private val searchText: MutableLiveData<String?> = savedStateHandle.getLiveData<String?>("searchInputText", null)

    fun submitSearchText(searchText: String?) {
        this.searchText.value = searchText
    }

    val searchResults = searchText.distinctUntilChanged().switchMap { searchText ->
        liveData<Resource<List<Note>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(Resource.Loading())
            try {
                emitSource(noteRepository.searchNotes(searchText).map { Resource.Success(it) })
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun insertNote(note: Note) = viewModelScope.launch { noteRepository.addNote(note) }

    fun updateNote(note: Note) = viewModelScope.launch { noteRepository.updateNote(note) }

    fun deleteNote(note: Note) = viewModelScope.launch { noteRepository.deleteNote(note) }

    fun addLabel(label: Label) = viewModelScope.launch { labelRepository.addLabel(label) }

    fun updateLabel(label: Label) = viewModelScope.launch { labelRepository.updateLabel(label) }

    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            labelRepository.deleteLabel(selectedLabel())
        }
        selectLabel(null)
    }
}