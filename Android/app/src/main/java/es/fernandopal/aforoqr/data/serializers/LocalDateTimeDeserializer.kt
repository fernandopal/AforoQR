package es.fernandopal.aforoqr.data.serializers
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


internal class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        Log.d("LocalDateTimeDeserializer", "Received LDT to parse: ${json.asString}")
        val locale = Locale("es", "ES")
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S").withLocale(locale)) } catch (e: Exception) {
        return try { LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withLocale(locale)) } catch (e: Exception) {
            Log.e("LocalDateTimeDeserializer", "Error parsing LDT: $e")
            LocalDateTime.now()
        }}}}}}}
    }
}