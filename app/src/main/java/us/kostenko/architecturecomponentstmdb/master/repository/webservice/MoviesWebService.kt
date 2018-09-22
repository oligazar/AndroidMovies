package us.kostenko.architecturecomponentstmdb.master.repository.webservice

import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import us.kostenko.architecturecomponentstmdb.common.api.API_KEY
import us.kostenko.architecturecomponentstmdb.master.model.Movies


interface MoviesWebService {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */

    @GET("movie/upcoming")
    fun getMovies(@Query("api_key") apiKey: String = API_KEY,
                  @Query("language") language: String = "en-US",
                  @Query("page") page: Int = 1,
                  @Query("region") region: String? = null): Deferred<Movies>
}