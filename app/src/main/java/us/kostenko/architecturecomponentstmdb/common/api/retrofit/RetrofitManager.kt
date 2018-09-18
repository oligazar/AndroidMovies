package us.kostenko.architecturecomponentstmdb.common.api.retrofit

import android.content.Context
import retrofit2.Retrofit
import us.kostenko.architecturecomponentstmdb.common.api.SingletonHolder
import us.kostenko.architecturecomponentstmdb.common.api.TmdbRetrofitBuilder

/**
 * Users singleton (backed by SingletonHolder) Retrofit instance
 * and helps to instantiate a Retrofit Service from the interface
 */
class RetrofitManager {

    companion object: SingletonHolder<Context, Retrofit>({ context ->
         TmdbRetrofitBuilder(context)
                 .buildRetrofit() }) {

        fun <S> createService(context: Context, serviceClass: Class<S>): S =
                instance(context).create(serviceClass)
    }
}