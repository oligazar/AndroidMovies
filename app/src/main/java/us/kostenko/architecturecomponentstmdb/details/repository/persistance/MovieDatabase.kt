package us.kostenko.architecturecomponentstmdb.details.repository.persistance

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import us.kostenko.architecturecomponentstmdb.common.api.SingletonHolder
import us.kostenko.architecturecomponentstmdb.common.database.DateConverter
import us.kostenko.architecturecomponentstmdb.common.database.GenresTypeConverter
import us.kostenko.architecturecomponentstmdb.common.database.IntListTypeConverter
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import java.util.*

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, GenresTypeConverter::class, IntListTypeConverter::class)
abstract class MovieDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

class MovieDatabaseHolder {
    companion object: SingletonHolder<Application, MovieDao>({ application ->
                 Room.databaseBuilder(application, MovieDatabase::class.java, "movie-database")
                    .fallbackToDestructiveMigration().build().movieDao() })
}

@Dao
interface MovieDao {

    @Insert(onConflict = REPLACE)
    fun save(movie: Movie)

    @Query("SELECT * from movie WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("SELECT * FROM movie WHERE id = :id AND dateUpdate > :dateUpdate LIMIT 1")
    fun hasMovie(id: Int, dateUpdate: Date): Movie?

    @Query("SELECT * FROM movie ORDER BY sort ASC")
    fun getMovies(): DataSource.Factory<Int, Movie>

    @Insert(onConflict = REPLACE)
    fun saveMovies(movies: ArrayList<Movie>)

    @Query("UPDATE movie SET liked= :isLiked WHERE id =:id")
    fun like(id: Int, isLiked: Boolean)
}