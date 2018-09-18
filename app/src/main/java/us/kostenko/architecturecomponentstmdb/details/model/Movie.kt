package us.kostenko.architecturecomponentstmdb.details.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Movie(
    @PrimaryKey
    var id: Int,
    var title: String,
    var vote_average: Double,
    var release_date: String,
    var poster_path: String,
    var overview: String,
    var original_language: String,
    var original_title: String,
    var genres: ArrayList<Genre>,
    var budget: Int,
    var lastRefresh: Date?,
    var liked: Boolean = false)

data class Genre(
    var id: Int,
    var name: String
)