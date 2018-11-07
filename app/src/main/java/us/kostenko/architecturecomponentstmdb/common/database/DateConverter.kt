package us.kostenko.architecturecomponentstmdb.common.database

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun Long?.toDate() = if (this != null) Date(this) else null

    @TypeConverter
    fun Date?.toTimestamp(): Long? = this?.time
}