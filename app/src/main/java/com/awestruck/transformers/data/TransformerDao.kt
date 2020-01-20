package com.awestruck.transformers.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.awestruck.transformers.model.Transformer

@Dao
interface TransformerDao {

    @Query("SELECT * FROM transformers")
    fun getAll(): LiveData<List<Transformer>>

    @Query("SELECT * FROM transformers WHERE id = :id")
    fun getTransformerById(id: String): LiveData<Transformer>

    @Delete
    fun delete(transformer: Transformer)

    @Update
    fun updateTransformer(vararg transformer: Transformer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransformer(vararg transformer: Transformer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransformers( transformer: List<Transformer>)
}