package com.ekosoftware.notas.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notesTable")
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long?,

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "content")
    var content: String?,

    @ColumnInfo(name = "labelId")
    var labelId: Long?

) : Parcelable

