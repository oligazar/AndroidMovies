package us.kostenko.architecturecomponentstmdb.master.repository

import androidx.room.Room
import org.junit.After
import org.junit.Before
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.inject
import org.koin.test.KoinTest
import us.kostenko.architecturecomponentstmdb.common.database.MovieDatabase
import us.kostenko.architecturecomponentstmdb.master.repository.persistance.MasterDao

class MasterDatabaseTest: KoinTest {

    private val database: MovieDatabase by inject()
    private val masterDAO: MasterDao by inject()

    private val testModule = module {
        single (override = true) {
            Room.inMemoryDatabaseBuilder(get(), MovieDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
        }
    }

    @Before
    fun setUp() {
        loadKoinModules(testModule)
    }

    @After
    fun tearDown() {
        database.close()
    }

}