package us.kostenko.architecturecomponentstmdb.common.database

import androidx.room.TypeConverter


class IntListTypeConverter {

    @TypeConverter
    fun intsToString(ints: ArrayList<Int>?): String? = ints?.joinToString(",")


    @TypeConverter
    fun stringToInts(genres: String?) = ArrayList(genres?.split(",")?.asSequence()
                                 ?.map { it.toIntOrNull() }
                                 ?.filterNotNull()?.toList()
                                 ?: listOf())
}