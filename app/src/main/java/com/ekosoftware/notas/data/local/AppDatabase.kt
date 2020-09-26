package com.ekosoftware.notas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note

@Database(entities = [Note::class, Label::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun labelDao(): LabelDao
}