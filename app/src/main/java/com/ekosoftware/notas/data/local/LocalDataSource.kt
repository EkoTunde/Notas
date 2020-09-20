package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ekosoftware.notas.data.model.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val noteDao: NoteDao, private val labelDao: LabelDao) {

    fun getAllNotes(): LiveData<List<Note>> = noteDao.getAllNotes().map { it.asNoteList() }

    fun getNotesByLabel(label: String?): LiveData<List<Note>> = noteDao.getNotesByLabel(label).map { it.asNoteList() }

    fun searchNotes(search: String?): LiveData<List<Note>> = noteDao.searchNotes(search).map { it.asNoteList() }

    suspend fun addNote(note: Note) = noteDao.insertNote(note.asEntity())

    suspend fun updateNote(note: Note) = noteDao.updateNote(note.asEntity())

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note.asEntity())

    suspend fun getLabels(): List<Label> = labelDao.getLabels().asLabelList()

    suspend fun insertLabel(label: Label) = labelDao.insertLabel(label.asEntity())

    suspend fun updateLabel(label: Label) = labelDao.updateLabel(label.asEntity())

    suspend fun deleteLabel(label: Label) = labelDao.deleteLabel(label.asEntity())

}