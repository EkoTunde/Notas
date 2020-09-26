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
    }

    val currentSelectedLabelId: MutableLiveData<Long?> =
        savedStateHandle.getLiveData<Long?>("selectedLabelId", sharedPref().getLong(ARG_SELECTED_LABEL_ID, -1))

    val currentSelectedLabelName: MutableLiveData<String?> =
        savedStateHandle.getLiveData("selectedLabelName", sharedPref().getString(ARG_SELECTED_LABEL_ID, null))

    fun selectLabel(label: Label?) {
        currentSelectedLabelId.value = label?.id
        currentSelectedLabelName.value = label?.name
    }

    val labels = liveData<Resource<List<Label>>>(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emitSource(labelRepository.getLabels().map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    private fun sharedPref() =
        appContext.getSharedPreferences(appContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    val selectedNote: MutableLiveData<Note?> = savedStateHandle.getLiveData<Note?>("selectedNote", null)

    fun selectNote(note: Note?) {
        selectedNote.value = note
    }

    val notes = currentSelectedLabelId.distinctUntilChanged().switchMap { labelId ->
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

    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun addLabel(label: Label) = viewModelScope.launch {
        labelRepository.addLabel(label)
    }

    fun updateLabel(label: Label) = viewModelScope.launch {
        labelRepository.updateLabel(label)
    }

    fun deleteLabel(label: Label) = viewModelScope.launch {
        labelRepository.deleteLabel(label)
    }

    val errorEventReceiver = MutableLiveData<Resource<Note>>()

    private fun dispatchEvent(exception: Exception) {
        errorEventReceiver.value = Resource.Failure(exception)
    }

    override fun onCleared() {
        super.onCleared()
        with(sharedPref().edit()) {
            putLong(ARG_SELECTED_LABEL_ID, currentSelectedLabelId.value ?: -1)
            putString(ARG_SELECTED_LABEL_NAME, currentSelectedLabelName.value)
            apply()
        }
    }
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

/*val selectedLabel = currentSelectedLabelId.distinctUntilChanged().switchMap { labelId ->
        liveData<Resource<String>?>(viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(Resource.Loading())
            try {
                if (labelId == null) {
                    emit(null)
                } else {
                    val result = labelRepository.getLabelById(labelId)
                    emit(Resource.Success(result.name))
                }
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }*/