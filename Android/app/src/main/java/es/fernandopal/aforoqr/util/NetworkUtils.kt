/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.fernandopal.aforoqr.data.RetrofitClientInstance
import es.fernandopal.aforoqr.data.request.ApiEndpoints
import java.net.HttpURLConnection
import java.net.URL

class NetworkUtils {
    private val auth: FirebaseAuth = Firebase.auth
    private val api = RetrofitClientInstance.retrofitInstance!!.create(ApiEndpoints::class.java)

    fun ping(address: String?, port: Int, timeout: Int): Boolean {
        val url = "$address:$port${Const.API_PING_RESOURCE}"
        var result = true
        try {
            val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = timeout
            connection.connect()
            Log.e("", connection.responseCode.toString())
        } catch (e: Exception) {
            result = false
            Log.e("", e.message.toString())
        }
        return result
    }

    fun getApi(): ApiEndpoints { return api }
    fun getAuth(): FirebaseAuth { return auth }

    fun requestWithAuth(onSuccess: (token: String, uid: String) -> Unit) {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) onSuccess("Bearer ${it.result.token}", auth.currentUser!!.uid)
        }
    }

    fun requestWithAuth(onSuccess: (token: String, uid: String) -> Unit, onError: (error: String) -> Unit) {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (!it.isSuccessful || it.exception != null) onError(it.exception?.message!!)
            if (it.isSuccessful) onSuccess("Bearer ${it.result.token}", auth.currentUser!!.uid)
        }
    }
}