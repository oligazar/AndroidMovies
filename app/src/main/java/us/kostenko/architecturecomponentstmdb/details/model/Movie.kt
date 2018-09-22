package us.kostenko.architecturecomponentstmdb.details.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*


@Entity
data class Movie(
    @PrimaryKey
    var id: Int,
    var title: String,
    @SerializedName("vote_average")
    var voteAverage: Double,
    @SerializedName("release_date")
    var releaseDate: String,
    @SerializedName("poster_path")
    var posterPath: String,
    var overview: String,
    @SerializedName("original_language")
    var originalLanguage: String,
    @SerializedName("original_title")
    var originalTitle: String,
    var genres: ArrayList<Genre>,
    @SerializedName("genre_ids")    // TODO: transform genres to genreIds and genreNames
    var genreIds: ArrayList<Int>? = null,
    var budget: Int,
    var lastRefresh: Date?,
    var liked: Boolean = false
                )

data class Genre(
    var id: Int,
    var name: String
)

//data class Movie(
//    @PrimaryKey
//    var id: Int,
//    var title: String,
//    var vote_average: Double,
//    var release_date: String,
//    var poster_path: String,
//    var overview: String,
//    var original_language: String,
//    var original_title: String,
//    var genres: ArrayList<Genre>,
//    var budget: Int,
//    var lastRefresh: Date?,
//    var liked: Boolean = false)