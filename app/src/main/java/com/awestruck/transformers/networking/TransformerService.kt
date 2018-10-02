package com.awestruck.transformers.networking

import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.model.Transformers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

/**
 * Created by Chris on 2018-09-29.
 */
interface TransformerService {

    companion object {

        private const val BASE_URL = "https://transformers-api.firebaseapp.com/"

        private val retrofit by lazy { getClient() }

        private fun getClient(): Retrofit {

            val builder = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor())

            val client = builder.build()

            val gson = GsonBuilder()
                    .registerTypeAdapter(Transformer::class.java, TransformerDeserializer())
                    .registerTypeAdapter(Transformer::class.java, TransformerSerializer())
                    .create()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()


        }

        fun create() = retrofit.create(TransformerService::class.java)

    }


    @GET("allspark")
    fun getAllSpark(): Observable<String>


    @GET("transformers")
    fun getAll(): Observable<Transformers>

    @POST("transformers")
    fun add(@Body transformer: Transformer): Observable<Transformer>

    @PUT("transformers")
    fun update(@Body transformer: Transformer): Observable<Transformer>

    @DELETE("transformers/{transformerId}")
    fun delete(@Path("transformerId") id: String): Observable<Unit>

}