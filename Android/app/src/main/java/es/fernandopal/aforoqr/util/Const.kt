/**
 * Copyright (c) 2022 Fernando Palomo <mail></mail>@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.util

object Const {
    const val API_BASE_URL = "http://api.aforoqr.xyz"
    const val API_HTTP_PORT = 26080
    const val API_PING_RESOURCE = "/favicon.ico"
    const val API_FULL_URL = "$API_BASE_URL:$API_HTTP_PORT/api/v1/"

    // Error messages
    const val API_NOT_ALIVE_ERROR = "En estos momentos no se puede conectar con el servidor, puede que no esté disponible o se encuentre bajo mantenimiento. Disculpe las molestias."
    const val GS_NOT_ALIVE_ERROR = "No se puede contactar con los servicios de google, comprueba tu conexión a internet."
}