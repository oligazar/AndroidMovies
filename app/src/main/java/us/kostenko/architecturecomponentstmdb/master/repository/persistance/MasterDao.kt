package us.kostenko.architecturecomponentstmdb.master.repository.persistance

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import java.util.ArrayList
import java.util.Date

@Dao
abstract class MasterDao {

    @Query("SELECT id, title, posterPath FROM movies ORDER BY sort ASC")
    abstract fun getMovies(): DataSource.Factory<Int, MovieItem>

    @Query("""UPDATE movies SET title = :title,
        releaseDate = :releaseDate,
        posterPath  = :posterPath,
        backdropPath = :backdropPath,
        overview = :overview,
        originalLanguage = :originalLanguage,
        originalTitle = :originalTitle,
        dateUpdate = :dateUpdate,
        sort = :sort WHERE id = :id""")
    abstract fun updateMaster(id: Int,
                     title: String,
                     releaseDate: String,
                     posterPath: String?,
                     backdropPath: String?,
                     overview: String,
                     originalLanguage: String,
                     originalTitle: String,
                     dateUpdate: Date,
                     sort: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun saveMaster(movie: Movie): Long

    /**
     * One by one Update if not succes - insert
     * Doesn't update like
     * or insert with ignore and then update those not inserted?
     * https://codereview.stackexchange.com/
     */
    @Transaction
    open fun saveMovies(movies: ArrayList<Movie>, date: Date = Date()) {
        movies.mapIndexed { index, movie ->
            val start = getNextIndex()
            movie.sort = start + index
            updateMaster(movie, date)
        }
    }

    @Transaction
    open fun clearAndSaveNew(movies: ArrayList<Movie>) {
        clear()
        saveMovies(movies, Date())
    }

    fun updateMaster(movie: Movie, date: Date) {
        movie.apply {
            dateUpdate = date
            val movieExists = saveMaster(movie) < 0
            if (movieExists) {
                updateMaster(id, title, releaseDate, posterPath, backdropPath, overview, originalLanguage, originalTitle, dateUpdate, sort)
            }
        }
    }

    @Query("DELETE FROM movies")
    abstract fun clear()

    @Query("SELECT MAX(sort) + 1 FROM movies")
    abstract fun getNextIndex(): Int
}