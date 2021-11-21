package com.bubul.col.launcher.core

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class VersionedFileDesc(var name : String, var size : Int, var date : String)

interface RemoteVersionedFileService {
    @GET("gamefiles.py")
    fun getFileDescs(@Query("type") type : String) : Call<List<VersionedFileDesc>>
}

class RemoteDataManager(private val baseUrl : String) {

    private lateinit var remoteVersionedFileService : RemoteVersionedFileService
    private val logger = LoggerFactory.getLogger( this.javaClass.name)

    fun init()
    {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BASIC }
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        remoteVersionedFileService = retrofit.create(RemoteVersionedFileService::class.java)
    }

    fun fetchAll(type : String): List<VersionedFileDesc> {
        logger.info("Fetching remote database")
        try {
            val response = remoteVersionedFileService.getFileDescs(type).execute()
            if (response.isSuccessful) {
                return response.body()!!
            }
        } catch (e : Exception) {

        }
        return listOf()
    }
}