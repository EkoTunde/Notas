package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.notas.data.local.LocalDataSource
import com.ekosoftware.notas.data.model.Note
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class NoteRepositoryImpl @Inject constructor(private val localDataSource: LocalDataSource) : NoteRepository {

    override fun getNotesByLabel(labelId: Long?): LiveData<List<Note>> = when (labelId) {
        null -> localDataSource.getAllNotes()
        else -> localDataSource.getNotesByLabel(labelId)
    }

    override fun searchNotes(search: String?): LiveData<List<Note>> = localDataSource.searchNotes(search)

    override suspend fun addNote(note: Note) = localDataSource.addNote(note)

    override suspend fun updateNote(note: Note) = localDataSource.updateNote(note)

    override suspend fun deleteNote(note: Note) = localDataSource.deleteNote(note)

}