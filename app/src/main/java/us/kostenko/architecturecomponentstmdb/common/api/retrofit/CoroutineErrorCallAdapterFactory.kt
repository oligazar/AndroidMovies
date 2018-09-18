package us.kostenko.architecturecomponentstmdb.common.api.retrofit


//class CoroutineErrorCallAdapterFactory private constructor() : CallAdapter.Factory() {
//
//    private val originalFactory by lazy { CoroutineCallAdapterFactory() }
//
//    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
//        val wrapped = originalFactory.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
//        return CoroutineCallAdapterWrapper(retrofit, wrapped)
//    }
//
//    private class CoroutineCallAdapterWrapper<R>(private val retrofit: Retrofit,
//                                          private val wrapped: CallAdapter<R, *>) : CallAdapter<R, Any> {
//
//        override fun responseType(): Type {
//            return wrapped.responseType()
//        }
//
//        @Suppress("UNCHECKED_CAST")
//        override fun adapt(call: Call<R>): Any {
//            val obj = wrapped.adapt(call)
//
//            // TODO: Not working properly for now.
//            // Requires coroutine exception handling understanding
//            return when (obj) {
//                is Deferred<*> ->  { throwable: Throwable ->
//                    throwable.asRetrofitException()
//                }
//                else           -> obj
//            }
//        }
//
//        private fun Throwable.asRetrofitException(): RetrofitException {
//            // We had non-200 http error
//            if (this is HttpException) {
//                val response = this.response()
//
//                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit)
//            }
//
//            // A network error happened
//            if (this is IOException) {
//                return RetrofitException.networkError(this)
//            }
//
//            // We don't know what happened. We need to simply convert to an unknown error
//            return RetrofitException.unexpectedError(this)
//        }
//    }
//
//    companion object {
//
//        fun create(): CallAdapter.Factory = CoroutineErrorCallAdapterFactory()
//    }
//}