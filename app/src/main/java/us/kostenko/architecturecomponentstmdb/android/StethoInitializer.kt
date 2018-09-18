package us.kostenko.architecturecomponentstmdb.android

import android.app.Application

interface StethoInitializer {
    fun init(application: Application)
}