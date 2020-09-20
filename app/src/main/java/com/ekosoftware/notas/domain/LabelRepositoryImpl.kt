package com.ekosoftware.notas.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.notas.data.local.LocalDataSource
import com.ekosoftware.notas.data.model.Label
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class LabelRepositoryImpl @Inject constructor(private val localDataSource: LocalDataSource) : LabelRepository {

    override suspend fun getLabels(): List<Label> = localDataSource.getLabels()

    override suspend fun addLabel(label: Label) = localDataSource.insertLabel(label)

    override suspend fun updateLabel(label: Label) = localDataSource.updateLabel(label)

    override suspend fun deleteLabel(label: Label) = localDataSource.deleteLabel(label)
}