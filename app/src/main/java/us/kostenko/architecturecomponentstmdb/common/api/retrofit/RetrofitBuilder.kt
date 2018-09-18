package us.kostenko.architecturecomponentstmdb.common.api.retrofit

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Base class that puts together components, which configured in subclass
 * in order to create Retrofit instance
 */
abstract class RetrofitBuilder {

    fun buildRetrofit(): Retrofit = Retrofit.Builder()
                .apply { configRetrofit() }
                .build()

    abstract fun buildConverterFactory(): Converter.Factory

    abstract fun buildOkHttpClient(cache: Cache): OkHttpClient

    abstract fun Retrofit.Builder.configRetrofit()
}
