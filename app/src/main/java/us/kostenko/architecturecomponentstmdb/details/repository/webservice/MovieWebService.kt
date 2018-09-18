package us.kostenko.architecturecomponentstmdb.details.repository.webservice

import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import us.kostenko.architecturecomponentstmdb.common.api.API_KEY
import us.kostenko.architecturecomponentstmdb.details.model.Movie

interface MovieWebService {
    /**
     * https://api.themoviedb.org/3/movie/260513?api_key=af64b54e13f4cd4ee4f1fba93a4d2952&language=en-US
     */

    @GET("movie/{movie_id}") fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"): Deferred<Movie>
}