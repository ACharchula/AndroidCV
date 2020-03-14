package pl.antonic.androidcv

import org.junit.Test

import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import pl.antonic.androidcv.image.processing.ImageProcessingImpl

class ImageProcessingTests : OpenCVTest() {

    private val photoPath = this.javaClass.classLoader!!.getResource("test.jpg").path

    @Test
    fun bgr2grayConversionTest() {
        val src= Imgcodecs.imread(photoPath, Imgcodecs.IMREAD_COLOR)
        ImageProcessingImpl.histogramEqualization(src)
    }

    @Test
    fun gray2bgrConversionTest() {
        val src = Imgcodecs.imread(photoPath, Imgcodecs.IMREAD_COLOR)
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
        ImageProcessingImpl.gaussianBlur(src, 10)
    }
}
