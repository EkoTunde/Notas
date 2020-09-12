package com.ekosoftware.notas.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val creationDate: Long
)

@Entity(tableName = "notesTable")
data class NoteEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: Long
)

fun List<NoteEntity>.asNoteList(): List<Note> = this.map {
    Note(it.id, it.title, it.content, it.creationDate)
}

fun NoteEntity.asNote() : Note = Note(this.id, this.title, this.content, this.creationDate)
fun Note.asEntity() : NoteEntity = NoteEntity(this.id, this.title, this.content, this.creationDate)

