package com.ekosoftware.notas.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var id: Int? = null,
    var title: String? = null,
    var content: String? = null,
    var label: String? = null
) : Parcelable

@Entity(tableName = "notesTable")
data class NoteEntity(

    @PrimaryKey/*(autoGenerate = true)*/
    @ColumnInfo(name = "id")
    var id: Int?,

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "content")
    var content: String?,

    @ColumnInfo(name = "label")
    var label: String?
)

fun List<NoteEntity>.asNoteList(): List<Note> = this.map {
    Note(it.id, it.title, it.content, it.label)
}

fun NoteEntity.asNote(): Note = Note(this.id, this.title, this.content, this.label)
fun Note.asEntity(): NoteEntity = NoteEntity(this.id, this.title, this.content,  this.label)

