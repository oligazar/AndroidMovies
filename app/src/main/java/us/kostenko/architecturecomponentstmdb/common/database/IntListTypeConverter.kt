package us.kostenko.architecturecomponentstmdb.common.database

import android.arch.persistence.room.TypeConverter


class IntListTypeConverter {

    @TypeConverter
    fun intsToString(ints: ArrayList<Int>?): String? = ints?.joinToString(",")


    @TypeConverter
    fun stringToInts(genres: String?) = ArrayList(genres?.split(",")?.asSequence()
                                 ?.map { it.toIntOrNull() }
                                 ?.filterNotNull()?.toList()
                                 ?: listOf())
}