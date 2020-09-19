package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.data.model.asEntity
import com.ekosoftware.notas.data.model.asNoteList

interface NoteRepository {
    fun getAllNotes(): LiveData<List<Note>>
    fun getNotesByLabel(label: String?): LiveData<List<Note>>
    fun searchNotes(search: String?): LiveData<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}