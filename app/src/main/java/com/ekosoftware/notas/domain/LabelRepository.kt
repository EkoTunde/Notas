package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.data.model.asEntity
import com.ekosoftware.notas.data.model.asNoteList

interface LabelRepository {
    suspend fun getLabels(): List<Label>
    suspend fun addLabel(label: Label)
    suspend fun updateLabel(label: Label)
    suspend fun deleteLabel(label: Label)
}