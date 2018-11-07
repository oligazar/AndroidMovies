package us.kostenko.architecturecomponentstmdb.details.model
import com.google.gson.annotations.SerializedName


data class MovieError(
    @SerializedName("status_code") var statusCode: Int,
    @SerializedName("status_message") var statusMessage: String
)