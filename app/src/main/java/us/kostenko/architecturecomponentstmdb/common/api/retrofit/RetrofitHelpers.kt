package us.kostenko.architecturecomponentstmdb.common.api.retrofit

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Cache
import retrofit2.converter.gson.GsonConverterFactory


fun createGsonConverterFactory(config: GsonBuilder.() -> Unit): GsonConverterFactory = GsonConverterFactory
        .create(GsonBuilder()
                        .apply { config() }
                        .create())

/**
 * Get cache from context for OkHttpClient
 */
fun Context.getCache(sizeMb: Int = 10): Cache {
    val cacheSize = sizeMb * 1024 * 1024 // 10 MiB
    return Cache(cacheDir, cacheSize.toLong())
}