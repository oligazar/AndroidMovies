package us.kostenko.architecturecomponentstmdb.details.repository.persistance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import us.kostenko.architecturecomponentstmdb.details.model.Genre
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import java.util.ArrayList
import java.util.Date

@Dao
abstract class DetailDao {

    @Query("""UPDATE movies SET title = :title,
        releaseDate = :releaseDate,
        posterPath  = :posterPath,
        backdropPath = :backdropPath,
        overview = :overview,
        originalLanguage = :originalLanguage,
        originalTitle = :originalTitle,
        genres = :genres,
        dateUpdate = :dateUpdate WHERE id = :id""")
    abstract fun updateMovie(id: Int,
                             title: String,
                             releaseDate: String,
                             posterPath: String?,
                             backdropPath: String?,
                             overview: String,
                             originalLanguage: String,
                             originalTitle: String,
                             genres: ArrayList<Genre>?,
                             dateUpdate: Date)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun saveMovie(movie: Movie): Long

    @Query("SELECT * from movies WHERE id = :id")
    abstract fun getMovie(id: Int): LiveData<Movie>

    @Query("SELECT * FROM movies WHERE id = :id AND dateUpdate > :dateUpdate LIMIT 1")
    abstract fun getMovieByDate(id: Int, dateUpdate: Date): Movie?

    @Query("UPDATE movies SET liked = :isLiked WHERE id = :id")
    abstract fun like(id: Int, isLiked: Boolean)

    /* Doesn't update like and sort fields */
    fun updateMovie(movie: Movie, date: Date) {
        movie.apply {
            dateUpdate = date
            val movieExists = saveMovie(movie) < 0
            if (movieExists) {
                updateMovie(id, title, releaseDate, posterPath, backdropPath, overview, originalLanguage, originalTitle, genres, dateUpdate)
            }
        }
    }
}