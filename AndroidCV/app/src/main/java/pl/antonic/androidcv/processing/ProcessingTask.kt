package pl.antonic.androidcv.processing

import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import java.io.Serializable

class ProcessingTask(var path: String) : Serializable {
    var serverProcessing: Boolean = false
    var methodWrapper = ImageProcessingMethodWrapper()
}