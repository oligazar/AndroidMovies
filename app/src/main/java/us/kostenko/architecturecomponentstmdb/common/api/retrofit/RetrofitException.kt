package us.kostenko.architecturecomponentstmdb.common.api.retrofit

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException

class RetrofitException internal constructor(message: String?,
                                             /** The request URL which produced the error.  */
                                             val url: String?,
                                             /** Response object containing status code, headers, body, etc.  */
                                             val response: Response<*>?,
                                             /** The event kind which triggered this error.  */
                                             val kind: Kind,
                                             exception: Throwable?,
                                             /** The Retrofit this request was executed on  */
                                             val retrofit: Retrofit?) : RuntimeException(message, exception) {

    /** Identifies the event kind which triggered a [RetrofitException].  */
    enum class Kind {
        /** An [IOException] occurred while communicating to the server.  */
        NETWORK,
        /** A non-200 HTTP status code was received from the server.  */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {
        val errorBody = response?.errorBody() ?: return null
        val converter: Converter<ResponseBody, T>? = retrofit?.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
        return converter?.convert(errorBody) ?: convert(type, errorBody)
    }

    private fun <T> convert(type: Class<T>, errorBody: ResponseBody): T {
        val string = errorBody.string()
        Timber.d("errorBody: $string")
        return Gson().fromJson(string, type)
    }

    companion object {
        fun httpError(url: String, response: Response<*>, retrofit: Retrofit?): RetrofitException {
            val message = response.code().toString() + " " + response.message()
            return RetrofitException(message, url, response, Kind.HTTP, null, null)
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception, null)
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception, null)
        }
    }

    /**
     *  // We don't know what happened. We need to simply convert to an unknown error
    if (response.isSuccessful) {
    val body = response.body()
    if (body == null || response.code() == 204) {
    ApiEmptyResponse()
    } else {
    ApiSuccessResponse(
    body = body,
    linkHeader = response.headers()?.get("link")
    )
    }
    } else {
    val msg = response.errorBody()?.string()
    val errorMsg = if (msg.isNullOrEmpty()) {
    response.message()
    } else {
    msg
    }
    ApiErrorResponse(errorMsg ?: "unknown error")
    }
     */
}

fun Throwable.asRetrofitException(): RetrofitException {
    return when (this) {
        is HttpException -> {
            val response = response()
            RetrofitException.httpError(response.raw().request().url().toString(), response, null)
        }
        is IOException   -> {
            RetrofitException.networkError(this)
        }
        else             -> RetrofitException.unexpectedError(this)
    }
}
