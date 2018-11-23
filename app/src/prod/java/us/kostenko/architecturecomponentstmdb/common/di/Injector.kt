package us.kostenko.architecturecomponentstmdb.common.di

import android.content.Context
import us.kostenko.architecturecomponentstmdb.common.AndroidCoroutines
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.api.retrofit.RetrofitManager
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService


/**
 * TODO: to be moved to different flavor(debug, release)
 */

object Injector: Injection() {

    override fun provideCoroutines(): Coroutines = AndroidCoroutines()

    override fun provideDatab1ase(context: Context): MovieDatabase = MovieDatabase.instance(context)

    override fun provideMasterWebService(context: Context): MoviesWebService {
        return RetrofitManager.createService(context, MoviesWebService::class.java)
    }

    override fun provideDetailWebService(context: Context): MovieWebService {
        return RetrofitManager.createService(context, MovieWebService::class.java)
    }
}