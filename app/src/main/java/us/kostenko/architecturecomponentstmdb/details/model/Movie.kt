package us.kostenko.architecturecomponentstmdb.details.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*


@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    var id: Int,
    var title: String,
    @SerializedName("release_date")
    var releaseDate: String,
    @SerializedName("poster_path")
    var posterPath: String? = null,
    @SerializedName("backdrop_path")
    var backdropPath: String? = null,
    var overview: String,
    @SerializedName("original_language")
    var originalLanguage: String,
    @SerializedName("original_title")
    var originalTitle: String,
    var genres: ArrayList<Genre>? = null,
    var dateUpdate: Date = Date(),

    var liked: Boolean = false,
    var sort: Int
)
//@ColumnInfo(name = "first_name")

data class Genre(
    var id: Int,
    var name: String
)

//data class MovieDetail(
//    @PrimaryKey
//    var id: Int,
//    var title: String,
//    @SerializedName("release_date")
//    var releaseDate: String,
//    @SerializedName("poster_path")
//    var posterPath: String? = null,
//    @SerializedName("backdrop_path")
//    var backdropPath: String? = null,
//    var overview: String,
//    @SerializedName("original_language")
//    var originalLanguage: String,
//    @SerializedName("original_title")
//    var originalTitle: String,
//    var genres: ArrayList<Genre>? = null,
//    var dateUpdate: Date = Date()) {
//
//    constructor(m: Movie): this(m.id, m.title, m.releaseDate, m.posterPath, m.backdropPath, m.overview, m.originalLanguage, m.originalTitle, m.genres, m.dateUpdate )
//}