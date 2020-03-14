package pl.antonic.androidcv.methods

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageProcessingMethodWrapper : Serializable {

    @SerializedName("methods")
    @Expose
    val methods = mutableListOf<ImageProcessingMethod>()

    fun containsId(id: Methods) : Boolean {
        for (method in methods) {
            if (method.id == id)
                return true
        }

        return false
    }

    fun getMethod(id: Methods) : ImageProcessingMethod? {
        for (method in methods) {
            if (method.id == id)
                return method
        }

        return null
    }
}