package com.awestruck.transformers.networking

import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.model.Transformers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

/**
 * Created by Chris on 2018-09-29.
 */
interface TransformerService {

//    companion object {
//
//        private const val BASE_URL = "https://transformers-api.firebaseapp.com"
//
//        private val retrofit by lazy { getClient() }
//
//        private fun getClient(): Retrofit {
//
//            val builder = OkHttpClient.Builder()
//                    .addInterceptor(AuthInterceptor())
//                    .addInterceptor(HttpLoggingInterceptor())
//
//            val client = builder.build()
//
//            val gson = GsonBuilder()
//                    .registerTypeAdapter(Transformer::class.java, TransformerDeserializer())
//                    .registerTypeAdapter(Transformer::class.java, TransformerSerializer())
//                    .create()
//
//            return Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(client)
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .build()
//
//
//        }
//
//        fun create() = retrofit.create(TransformerService::class.java)
//
//    }


    @GET("allspark")
    suspend fun getAllSpark(): String

    @GET("transformers")
    suspend fun getAll(): Response<Transformers>

    @POST("transformers")
    suspend fun add(@Body transformer: Transformer): Response<Transformer>

    @PUT("transformers")
    suspend fun update(@Body transformer: Transformer): Response<Transformer>

    @DELETE("transformers/{transformerId}")
    suspend fun delete(@Path("transformerId") id: String): Response<Unit>

}