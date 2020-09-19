package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Query
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.data.model.NoteEntity
import com.ekosoftware.notas.data.model.asEntity
import com.ekosoftware.notas.data.model.asNoteList
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val noteDao: NoteDao) {

    fun getAllNotes(): LiveData<List<Note>> = noteDao.getAllNotes().map { it.asNoteList() }

    fun getNotesByLabel(label: String?): LiveData<List<Note>> = noteDao.getNotesByLabel(label).map { it.asNoteList() }

    fun searchNotes(search: String?): LiveData<List<Note>> = noteDao.searchNotes(search).map { it.asNoteList() }

    suspend fun addNote(note: Note) = noteDao.insertNote(note.asEntity())

    suspend fun updateNote(note: Note) = noteDao.updateNote(note.asEntity())

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note.asEntity())

}