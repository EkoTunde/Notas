package com.ekosoftware.notas.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Label(val id: Int?, val name: String) : Parcelable

@Entity(tableName = "labelsTable")
data class LabelEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "name")
    val name: String
)

fun Label.asEntity() = LabelEntity(this.id, this.name)
fun LabelEntity.asLabel() = LabelEntity(this.id, this.name)
fun Array<LabelEntity>.asLabelList(): List<Label> = this.map {
    Label(it.id, it.name)
}