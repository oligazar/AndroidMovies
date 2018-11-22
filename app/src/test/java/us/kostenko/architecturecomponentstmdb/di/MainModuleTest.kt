package us.kostenko.architecturecomponentstmdb.di

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.KoinTest
import org.koin.test.checkModules

class MainModuleTest: KoinTest {

    private val mockAndroid = module {
        single { mock<Context>() }
    }

    @Test
    fun testAppModule() {
        checkModules(onlineAppModules + mockAndroid)
    }
}