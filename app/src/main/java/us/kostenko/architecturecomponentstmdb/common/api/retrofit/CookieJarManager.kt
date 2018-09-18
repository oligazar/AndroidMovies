package us.kostenko.architecturecomponentstmdb.common.api.retrofit

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import us.kostenko.architecturecomponentstmdb.common.api.SingletonHolder

/**
 * Handles cookies for retrofit requests
 */
class CookieJarManager {
    companion object: SingletonHolder<Context, PersistentCookieJar>(
            { context -> PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)) }
    )
}