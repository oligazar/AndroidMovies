package us.kostenko.architecturecomponentstmdb.master.repository

import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService

class MoviesRepository(private val webService: MoviesWebService) {

//    fun getMovies(): LiveData<ArrayList<Movie>> {
//        val ldMovies = MutableLiveData<ArrayList<Movie>>()
//        GlobalScope.launch {
//            val movies = webService.getMovies().await()
//            ldMovies.postValue(movies.results)
//        }
//        return ldMovies
//    }

//    suspend fun getMovies(): ArrayList<Movie> {
//        return webService.getMovies(1).results
//    }
}