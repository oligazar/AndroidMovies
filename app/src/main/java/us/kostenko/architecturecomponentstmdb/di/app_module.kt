package us.kostenko.architecturecomponentstmdb.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import us.kostenko.architecturecomponentstmdb.common.AndroidCoroutines
import us.kostenko.architecturecomponentstmdb.common.Coroutines
import us.kostenko.architecturecomponentstmdb.common.api.TmdbRetrofitBuilder
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepository
import us.kostenko.architecturecomponentstmdb.details.repository.MovieDetailRepositoryImpl
import us.kostenko.architecturecomponentstmdb.details.repository.webservice.MovieWebService
import us.kostenko.architecturecomponentstmdb.details.viewmodel.MovieDetailViewModel
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepository
import us.kostenko.architecturecomponentstmdb.master.repository.MoviesRepositoryImpl
import us.kostenko.architecturecomponentstmdb.master.repository.webservice.MoviesWebService
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MovieItemViewModel
import us.kostenko.architecturecomponentstmdb.master.viewmodel.MoviesViewModel


val baseModule = module {

    // Coroutines
    factory<Coroutines> { AndroidCoroutines() }

    // MovieDatabase
    single { Room.databaseBuilder(androidContext(), MovieDatabase::class.java, "movies-db")
            /*.fallbackToDestructiveMigration()*/.build() }

    // Retrofit
    single { TmdbRetrofitBuilder(androidContext()).buildRetrofit() }
}

val movieMasterModule = module {

    // MasterDao
    single { get<MovieDatabase>().masterDao() }

    // MoviesWebService
    single<MoviesWebService>{ get<Retrofit>().create(MoviesWebService::class.java) }

    // MoviesRepository
    single<MoviesRepository>{ MoviesRepositoryImpl(get(), get()) }

    // MoviesViewModel
    viewModel { MoviesViewModel(get()) }

    // MovieItemViewModel
    viewModel { MovieItemViewModel() }
}

val movieDetailModule = module {

    // DetailDao
    single { get<MovieDatabase>().detailDao() }

    // MovieWebService
    single<MovieWebService>{ get<Retrofit>().create(MovieWebService::class.java) }

    // MovieDetailRepository
    single<MovieDetailRepository>{ MovieDetailRepositoryImpl(get(), get(), get()) }

    // MovieDetailViewModel
    viewModel { MovieDetailViewModel(get(), get()) }
}

val onlineAppModules = listOf(baseModule, movieMasterModule, movieDetailModule)