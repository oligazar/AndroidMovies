package us.kostenko.architecturecomponentstmdb.details.repository.persistance

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import us.kostenko.architecturecomponentstmdb.common.database.DateConverter
import us.kostenko.architecturecomponentstmdb.common.database.GenresTypeConverter
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import java.util.*

@Database(entities = [Movie::class], version = 1)
@TypeConverters(DateConverter::class, GenresTypeConverter::class)
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

    @Query("UPDATE movie SET liked= :isLiked WHERE id =:id")
    fun like(id: Int, isLiked: Boolean)
}