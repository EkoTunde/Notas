package com.ekosoftware.notas.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.notas.data.model.LabelEntity

@Dao
interface LabelDao {

    @Query("SELECT * FROM labelsTable ORDER BY name")
    suspend fun getLabels(): Array<LabelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(vararg labels: LabelEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLabel(vararg labels: LabelEntity)

    @Delete
    suspend fun deleteLabel(vararg labels: LabelEntity)
} 