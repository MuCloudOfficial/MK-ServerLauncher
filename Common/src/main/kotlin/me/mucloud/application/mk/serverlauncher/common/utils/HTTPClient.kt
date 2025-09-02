package me.mucloud.application.mk.serverlauncher.common.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

object HTTPClient {

    private val instance = OkHttpClient()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     *  # | Utils
     *
     *  ## Get Body Message by Specified URL
     *
     * @param url Request URL
     * @return A Request body as [String]
     */
    fun getBody(url: String): String{
        val request = Request.Builder().url(url).build()
        instance.newCall(request).execute().use { i ->
            return i.body.string()
        }
    }

    /**
     * # | Utils
     *
     * ## Get Body Message by Specified URL
     *
     * @param url Request URL
     * @return A Request body as [JsonObject]
     */
    fun getJsonObject(url: String): JsonObject{
        val request = Request.Builder().url(url).build()
        instance.newCall(request).execute().use { i ->
            return gson.toJsonTree(i.body.string()).asJsonObject
        }
    }

}