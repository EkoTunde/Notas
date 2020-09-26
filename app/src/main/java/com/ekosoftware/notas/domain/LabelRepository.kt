package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.notas.data.model.Label

interface LabelRepository {
    suspend fun getLabelById(labelId: Long): Label
    suspend fun getLabels(): LiveData<List<Label>>
    suspend fun addLabel(label: Label)
    suspend fun updateLabel(label: Label)
    suspend fun deleteLabel(label: Label)
}