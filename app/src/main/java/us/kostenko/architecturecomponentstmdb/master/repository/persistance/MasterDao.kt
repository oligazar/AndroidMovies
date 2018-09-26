package us.kostenko.architecturecomponentstmdb.master.repository.persistance

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import timber.log.Timber
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.master.model.MovieItem
import java.util.*

@Dao
interface MasterDao {

    @Query("SELECT id, title, posterPath FROM movies ORDER BY sort ASC")
    fun getMovies(): DataSource.Factory<Int, MovieItem>

    @Query("""UPDATE movies SET title = :title,
        releaseDate = :releaseDate,
        posterPath  = :posterPath,
        backdropPath = :backdropPath,
        overview = :overview,
        originalLanguage = :originalLanguage,
        originalTitle = :originalTitle,
        dateUpdate = :dateUpdate,
        sort = :sort WHERE id = :id""")
    fun updateMaster(id: Int,
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
    fun saveMasterOrIgnore(movie: Movie): Long

    @Transaction
    fun saveMovies(movies: ArrayList<Movie>) {
        movies.forEach { it.run {
            val savedId = saveMasterOrIgnore(it)
            Timber.d("inserted savedId: $savedId")
            if (savedId < 0) {
                updateMaster(id, title, releaseDate, posterPath, backdropPath, overview, originalLanguage, originalTitle, dateUpdate, sort)
                Timber.d("updated id: $id")
            }
        } }
    }

    /**
     * One by one Update if not succes - insert
     *
     * or insert with ignore and then update those not inserted?
     * https://codereview.stackexchange.com/
     */
}