package com.awestruck.transformers.data

import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.networking.TransformerService

class TransformerRemoteDataSource constructor(private val service: TransformerService) : BaseDataSource() {

    suspend fun fetchTransfomrers()
            = getResult { service.getAll() }

    suspend fun updateTransformer(t: Transformer) = service.update(t)
    suspend fun addTransformer(t: Transformer) = service.add(t)
    suspend fun deleteTransformer(id: String) = service.delete(id)
}