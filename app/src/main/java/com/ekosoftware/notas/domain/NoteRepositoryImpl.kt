package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.notas.data.local.LocalDataSource
import com.ekosoftware.notas.data.model.Note
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class NoteRepositoryImpl @Inject constructor(private val localDataSource: LocalDataSource) : NoteRepository {

    override fun getAllNotes(): LiveData<List<Note>> = localDataSource.getAllNotes()

    override fun getNotesByLabel(label: String?): LiveData<List<Note>> = localDataSource.getNotesByLabel(label)

    override fun searchNotes(search: String?): LiveData<List<Note>> = localDataSource.searchNotes(search)

    override suspend fun addNote(note: Note) = localDataSource.addNote(note)

    override suspend fun updateNote(note: Note) = localDataSource.updateNote(note)

    override suspend fun deleteNote(note: Note) = localDataSource.deleteNote(note)

    fun prueba(label: String? = null, search: String? = null): LiveData<List<Note>> {
        label?.let {
            return localDataSource.getNotesByLabel(it)
        }
        search?.let {
            return localDataSource.searchNotes(search)
        }
        return getAllNotes()
    }
}