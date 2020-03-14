package pl.antonic.androidcv.methods

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageProcessingMethod : Serializable {

    @SerializedName("id")
    @Expose
    var id: Methods

    @SerializedName("arguments")
    @Expose
    var arguments: List<String>

    constructor(id: Methods, arguments: List<String>) {
        this.id = id
        this.arguments = arguments
    }
}