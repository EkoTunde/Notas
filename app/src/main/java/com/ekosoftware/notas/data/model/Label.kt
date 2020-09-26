package com.ekosoftware.notas.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "labelsTable")
data class Label(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String?

) : Parcelable

fun List<Label>.getLabelById(id: Long?): Label? {
    if (id == null) return null
    for (label in this) {
        if (label.id == id) {
            return label
        }
    }
    return null
}

fun List<Label>.getLabelByName(name: String?): Label? {
    for (label in this) {
        if (label.name == name) {
            return label
        }
    }
    return null
}