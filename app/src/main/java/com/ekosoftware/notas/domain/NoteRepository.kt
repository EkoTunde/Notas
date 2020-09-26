package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.notas.data.model.Note

interface NoteRepository {
    fun getNotesByLabel(labelId: Long?): LiveData<List<Note>>
    fun searchNotes(search: String?): LiveData<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}