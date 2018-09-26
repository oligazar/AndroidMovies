package us.kostenko.architecturecomponentstmdb.details.repository.persistance

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import us.kostenko.architecturecomponentstmdb.details.model.Genre
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import java.util.*

@Dao
interface DetailDao {

    @Query("""UPDATE movies SET title = :title,
        releaseDate = :releaseDate,
        posterPath  = :posterPath,
        backdropPath = :backdropPath,
        overview = :overview,
        originalLanguage = :originalLanguage,
        originalTitle = :originalTitle,
        genres = :genres,
        dateUpdate = :dateUpdate WHERE id = :id""")
    fun updateDetail(id: Int,
                     title: String,
                     releaseDate: String,
                     posterPath: String?,
                     backdropPath: String?,
                     overview: String,
                     originalLanguage: String,
                     originalTitle: String,
                     genres: ArrayList<Genre>?,
                     dateUpdate: Date)

    @Query("SELECT * from movies WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("SELECT * FROM movies WHERE id = :id AND dateUpdate > :dateUpdate LIMIT 1")
    fun hasMovie(id: Int, dateUpdate: Date): Movie?

    @Query("UPDATE movies SET liked = :isLiked WHERE id = :id")
    fun like(id: Int, isLiked: Boolean)
}