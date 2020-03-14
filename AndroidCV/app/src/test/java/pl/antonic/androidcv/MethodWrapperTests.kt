package pl.antonic.androidcv

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import pl.antonic.androidcv.methods.ImageProcessingMethod
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.methods.Methods

class MethodWrapperTests {

    @Test
    fun serializeMethodWrapperTest() {
        val methodWrapper = ImageProcessingMethodWrapper()
        methodWrapper.methods.add(ImageProcessingMethod(Methods.HISTOGRAM_EQUALIZATION, listOf()))
        methodWrapper.methods.add(ImageProcessingMethod(Methods.EROSION, listOf("10", "0")))
        val json = Gson().toJson(methodWrapper)
        val expected = "{\"methods\":[{\"id\":\"HISTOGRAM_EQUALIZATION\",\"arguments\":[]},{\"id\":\"EROSION\",\"arguments\":[\"10\",\"0\"]}]}"
        assertEquals(expected, json)
    }
}