/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data

import com.google.gson.*
import es.fernandopal.aforoqr.data.serializers.LocalDateTimeDeserializer
import es.fernandopal.aforoqr.data.serializers.LocalDateTimeSerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object RetrofitClientInstance {
    private var retrofit: Retrofit? = null
    // private const val BASE_URL = "http://10.0.2.2:26080/"
    private const val BASE_URL = "http://api.aforoqr.xyz:26080/"

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
                gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
                val gson = gsonBuilder.setLenient().setPrettyPrinting().create()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient.Builder().build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            }
            return retrofit
        }

}