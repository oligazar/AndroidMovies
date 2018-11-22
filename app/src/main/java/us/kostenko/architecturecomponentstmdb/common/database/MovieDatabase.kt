package us.kostenko.architecturecomponentstmdb.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import us.kostenko.architecturecomponentstmdb.common.api.SingletonHolder
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, GenresTypeConverter::class, IntListTypeConverter::class)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun detailDao(): DetailDao
    abstract fun masterDao(): MasterDao

    companion object: SingletonHolder<Context, MovieDatabase>({ context ->
              Room.databaseBuilder(context, MovieDatabase::class.java, "movies-db")
                      .fallbackToDestructiveMigration().build() })
}

