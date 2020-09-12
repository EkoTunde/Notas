package com.ekosoftware.notas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekosoftware.notas.data.model.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}