package us.kostenko.architecturecomponentstmdb.details.repository.persistance

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
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

@Dao
interface MovieDao {

    @Insert(onConflict = REPLACE)
    fun save(movie: Movie)

    @Query("SELECT * from movie WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("SELECT * FROM movie WHERE id = :id AND lastRefresh > :lastRefresh LIMIT 1")
    fun hasMovie(id: Int, lastRefresh: Date): Movie?

    @Query("SELECT * FROM movie WHERE id = :id")    // TODO: refine the query
    fun getMovies(id: Int): LiveData<Movie>

    @Query("UPDATE movie SET liked= :isLiked WHERE id =:id")
    fun like(id: Int, isLiked: Boolean)
}