package pl.antonic.androidcv.processing

import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.widget.ImageView
import org.opencv.android.Utils
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import pl.antonic.androidcv.image.processing.ImageProcessingImpl
import pl.antonic.androidcv.activities.ViewPictureActivity
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.methods.Methods
import pl.antonic.androidcv.presenters.ViewPictureActivityPresenter
import java.io.File

class PhotoClientProcessing(
    private val filePath: String,
    private val methodsWrapper: ImageProcessingMethodWrapper,
    private val idx: Int,
    private val presenter: ViewPictureActivityPresenter
) : AsyncTask<Void, ProcessingExceptions, Void>() {

    lateinit var bitmap : Bitmap


    override fun doInBackground(vararg p0: Void?): Void? {
        process(filePath, methodsWrapper)
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        presenter.setBitmapAndDecrement(idx, bitmap)
    }

    private fun process(filePath: String, methodsWrapper: ImageProcessingMethodWrapper) {
        var src = Imgcodecs.imread(Uri.parse(filePath).path, Imgcodecs.IMREAD_COLOR)

        for (method in methodsWrapper.methods) {
            when (method.id) {
                Methods.CONTOURS_FINDING -> src = ImageProcessingImpl.contoursFinding(src, method.arguments[0].toInt())
                Methods.HISTOGRAM_EQUALIZATION -> src = ImageProcessingImpl.histogramEqualization(src)
                Methods.BILATERAL_FILTER -> src = ImageProcessingImpl.bilateralFilter(src, method.arguments[0].toInt())
                Methods.GAUSSIAN_BLUR -> src = ImageProcessingImpl.gaussianBlur(src, method.arguments[0].toInt())
                Methods.EROSION -> src = ImageProcessingImpl.erosion(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.DILATION -> src = ImageProcessingImpl.dilation(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.OPENING -> src = ImageProcessingImpl.opening(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.CLOSING -> src = ImageProcessingImpl.closing(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.BLACK_HAT -> src = ImageProcessingImpl.blackHat(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.TOP_HAT -> src = ImageProcessingImpl.topHat(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.GRADIENT -> src = ImageProcessingImpl.gradient(src, method.arguments[1].toInt(), method.arguments[0].toInt())
                Methods.LINE_EXTRACT -> src = ImageProcessingImpl.horizontalLineExtract(src, method.arguments[0].toInt())
                Methods.BASIC_THRESHOLD -> src = ImageProcessingImpl.basicThreshold(src, method.arguments[0].toInt(), method.arguments[1].toInt())
                Methods.SOBEL_DERIVATIVES -> src = ImageProcessingImpl.sobelDerivatives(src)
                Methods.LAPLACE_OPERATOR -> src = ImageProcessingImpl.laplaceOperator(src)
                Methods.HISTOGRAM_CALCULATION -> src = ImageProcessingImpl.histogramCalculation(src)
                Methods.CONVEX_HULL -> src = ImageProcessingImpl.convexHull(src, method.arguments[0].toInt())
                Methods.CANNY_EDGE_DETECTOR -> src = ImageProcessingImpl.cannyEdgeDetector(src, method.arguments[0].toInt())
            }
        }
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGBA)
        bitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, bitmap)
    }
}