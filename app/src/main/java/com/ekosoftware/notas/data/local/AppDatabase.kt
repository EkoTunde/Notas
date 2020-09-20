package com.ekosoftware.notas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekosoftware.notas.data.model.LabelEntity
import com.ekosoftware.notas.data.model.NoteEntity

@Database(entities = [NoteEntity::class, LabelEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun labelDao(): LabelDao
}