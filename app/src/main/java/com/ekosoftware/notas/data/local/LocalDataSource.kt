package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ekosoftware.notas.data.model.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val noteDao: NoteDao, private val labelDao: LabelDao) {

    fun getAllNotes(): LiveData<List<Note>> = noteDao.getAllNotes()

    fun getNotesByLabel(labelId: Long?): LiveData<List<Note>> = noteDao.getNotesByLabel(labelId)

    fun searchNotes(search: String?): LiveData<List<Note>> = noteDao.searchNotes(search)

    suspend fun addNote(note: Note) = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun getLabelById(labelId: Long): Label = labelDao.getLabelById(labelId)

    fun getLabels(): LiveData<List<Label>> = labelDao.getLabels()

    suspend fun insertLabel(label: Label) = labelDao.insertLabel(label)

    suspend fun updateLabel(label: Label) = labelDao.updateLabel(label)

    suspend fun deleteLabel(label: Label) = labelDao.deleteLabel(label)

}