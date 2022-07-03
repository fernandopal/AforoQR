package es.fernandopal.aforoqr.data.request
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import es.fernandopal.aforoqr.data.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpoints {
    @GET("session/me")
    fun getUser(@Header("Authorization") authHeader: String?): Call<AppUser?>?

    @GET("api/v1/user")
    fun getAllUsers(@Header("Authorization") authHeader: String?): Call<List<AppUser>>?

    @POST("api/v1/user/update")
    fun saveUser(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<String>?

    @GET("api/v1/room")
    fun getAllRooms(@Header("Authorization") authHeader: String?): Call<List<Room>>?

    @POST("api/v1/room")
    fun saveRoom(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<Long?>?

    @DELETE("api/v1/room/{id}")
    fun deleteRoom(@Header("Authorization") authHeader: String?, @Path("id") id: Long): Call<String>?

    @GET("api/v1/room/{id}")
    fun getRoom(@Header("Authorization") authHeader: String?, @Path("id") id: Long): Call<Room?>?

    @GET("api/v1/reservation/u/{uid}")
    fun getUserReservations(@Header("Authorization") authHeader: String?, @Path("uid") uid: String): Call<List<Reservation>?>?

    @POST("api/v1/reservation/{id}/cancel")
    fun cancelReservation(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<String?>?

    @POST("api/v1/reservation/{id}/delete")
    fun deleteReservation(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<String?>?

    @POST("api/v1/reservation/")
    fun createReservation(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<Long?>?

    @GET("api/v1/stats/")
    fun getStats(@Header("Authorization") authHeader: String?): Call<Stats>?

    @POST("api/v1/qr/generate")
    fun generateQRCode(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<QRCode?>?

    @GET("api/v1/qr/{id}")
    fun getQRCode(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<QRCode?>?

    @GET("api/v1/qr/{id}")
    fun getQRForRoom(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<QRCode?>?

    @POST("api/v1/reservation/{id}/confirm")
    fun confirmReservation(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<String?>?

    @POST("api/v1/reservation/{id}/send-confirmation-email")
    fun sendConfirmationEmail(@Header("Authorization") authHeader: String?, @Path("id") id: Long?): Call<String?>?

    @GET("api/v1/reservation/{id}")
    fun getReservation(@Header("Authorization") authHeader: String, @Path("id") id: Long): Call<Reservation?>?

    @POST("api/v1/reservation/complex")
    fun getReservationComplex(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<List<Reservation>?>?

    @POST("api/v1/search")
    fun searchEventsAndRooms(@Header("Authorization") authHeader: String?, @Body requestBody: RequestBody): Call<Tuple<List<Room>, List<Event>>?>?
}