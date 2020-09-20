package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.data.model.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM notesTable")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notesTable WHERE label = :label ORDER BY id ASC")
    fun getNotesByLabel(label: String?): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notesTable WHERE title LIKE '%' || :search || '%' OR content LIKE '%' || :search || '%' ORDER BY id ASC")
    fun searchNotes(search: String?): LiveData<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
} 