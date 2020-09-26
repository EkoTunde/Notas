package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.notas.data.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notesTable")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE labelId = :labelId ORDER BY id ASC")
    fun getNotesByLabel(labelId: Long?): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE title LIKE '%' || :search || '%' OR content LIKE '%' || :search || '%' ORDER BY id ASC")
    fun searchNotes(search: String?): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
} 