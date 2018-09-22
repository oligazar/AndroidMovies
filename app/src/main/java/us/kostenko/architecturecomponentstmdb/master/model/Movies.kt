package us.kostenko.architecturecomponentstmdb.master.model

import com.google.gson.annotations.SerializedName
import us.kostenko.architecturecomponentstmdb.details.model.Movie
import java.util.*


data class Movies(
    @SerializedName("results")
    var results: ArrayList<Movie>,
    @SerializedName("page")
    var page: Int,
    @SerializedName("total_results")
    var totalResults: Int,
    @SerializedName("dates")
    var dates: Dates,
    @SerializedName("total_pages")
    var totalPages: Int
)

data class Dates(
    @SerializedName("maximum") var maximum: String,
    @SerializedName("minimum") var minimum: String
)