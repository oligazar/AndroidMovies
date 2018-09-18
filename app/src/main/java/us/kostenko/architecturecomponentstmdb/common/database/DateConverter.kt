package us.kostenko.architecturecomponentstmdb.common.database

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun Long.toDate() = Date(this)

    @TypeConverter
    fun Date.toTimestamp(): Long? = time
}