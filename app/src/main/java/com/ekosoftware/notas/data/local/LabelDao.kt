package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.notas.data.model.Label

@Dao
interface LabelDao {

    @Query("SELECT * FROM labelsTable WHERE id = :labelId LIMIT 1")
    suspend fun getLabelById(labelId: Long): Label

    @Query("SELECT * FROM labelsTable ORDER BY name")
    fun getLabels(): LiveData<List<Label>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: Label)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)
} 