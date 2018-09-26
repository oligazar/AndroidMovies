package us.kostenko.architecturecomponentstmdb.common.database

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import us.kostenko.architecturecomponentstmdb.common.api.SingletonHolder
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import us.kostenko.architecturecomponentstmdb.details.repository.persistance.DetailDao
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, GenresTypeConverter::class, IntListTypeConverter::class)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun detailDao(): DetailDao
    abstract fun masterDao(): MasterDao

    companion object: SingletonHolder<Application, MovieDatabase>({ application ->
              Room.databaseBuilder(application, MovieDatabase::class.java, "movies-db")
                      .fallbackToDestructiveMigration().build() })
}

