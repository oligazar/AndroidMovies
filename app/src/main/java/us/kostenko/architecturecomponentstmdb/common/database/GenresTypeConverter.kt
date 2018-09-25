package us.kostenko.architecturecomponentstmdb.common.database

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import us.kostenko.architecturecomponentstmdb.details.model.Genre


class GenresTypeConverter {

    @TypeConverter
    fun genresToJson(genres: ArrayList<Genre>?): String = Gson().toJson(genres)     // todo: type check


    @TypeConverter
    fun jsonToGenres(genres: String?): ArrayList<Genre>? {
        val genresListType = object: TypeToken<ArrayList<Genre>>() {}.type
        return Gson().fromJson<ArrayList<Genre>>(genres, genresListType)
    }
}