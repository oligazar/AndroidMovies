package us.kostenko.architecturecomponentstmdb.common.api

import android.content.Context
import com.google.gson.FieldNamingPolicy
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.*
import us.kostenko.architecturecomponentstmdb.common.di.Injector
import java.util.concurrent.TimeUnit

class TmdbRetrofitBuilder(private val context: Context): RetrofitBuilder() {

    override fun Retrofit.Builder.configRetrofit() {
        baseUrl(BASE_URL)
        // TODO: CoroutineCallAdapterFactory should be recompiled soon
        addCallAdapterFactory(CoroutineCallAdapterFactory())
        addConverterFactory(buildConverterFactory())
        client(buildOkHttpClient(context.getCache()))
    }

    override fun buildOkHttpClient(cache: Cache): OkHttpClient = Injector.provideOkHttpClient(cache) {
        connectTimeout(30, TimeUnit.SECONDS) // connect timeout
        readTimeout(30, TimeUnit.SECONDS)
        cookieJar(CookieJarManager.instance(context))
    }

    // TODO: Posibly change it to Mosbly: https://medium.com/@BladeCoder/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d
    override fun buildConverterFactory(): Converter.Factory = createGsonConverterFactory {
        //        registerTypeAdapter(TicketMenuRoot::class.java, RootItemDeserializer())
        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    }
}