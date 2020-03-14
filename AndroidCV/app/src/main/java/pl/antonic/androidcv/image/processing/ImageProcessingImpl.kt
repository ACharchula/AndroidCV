package pl.antonic.androidcv.image.processing

import org.opencv.core.*
import kotlin.random.Random
import org.opencv.imgproc.Imgproc.*
import org.opencv.core.Scalar
import org.opencv.core.MatOfFloat
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfInt

class ImageProcessingImpl {
    companion object {

        fun bilateralFilter(src: Mat, diameter: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val dst = Mat()
            bilateralFilter(bgrSrc, dst, diameter, (diameter * 2).toDouble(), (diameter / 2).toDouble())
            return dst
        }

        fun gaussianBlur(src: Mat, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val dst = Mat()
            GaussianBlur(bgrSrc, dst, Size((kernelSize.toDouble() * 2) + 1, (kernelSize.toDouble() * 2) + 1), 0.0, 0.0)
            return dst
        }

        fun erosion(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)

            val dst = Mat()
            erode(bgrSrc, dst, element)
            return dst
        }

        fun dilation(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)

            val dst = Mat()
            dilate(bgrSrc, dst, element)
            return dst
        }

        private fun getElement(selectedType: Int, kernelSize: Int) : Mat {
            var erosionType = 0

            when (selectedType) {
                0 -> erosionType = MORPH_RECT
                1 -> erosionType = MORPH_CROSS
                2 -> erosionType = MORPH_ELLIPSE
            }

            return getStructuringElement(erosionType, Size((2*kernelSize + 1).toDouble(),
                (2*kernelSize + 1).toDouble()), Point(kernelSize.toDouble(), kernelSize.toDouble()))
        }

        fun opening(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)
            val dst = Mat()
            morphologyEx(bgrSrc, dst, MORPH_OPEN, element)
            return dst
        }

        fun closing(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)
            val dst = Mat()
            morphologyEx(bgrSrc, dst, MORPH_CLOSE, element)
            return dst
        }

        fun gradient(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)
            val dst = Mat()
            morphologyEx(bgrSrc, dst, MORPH_GRADIENT, element)
            return dst
        }

        fun topHat(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)
            val dst = Mat()
            morphologyEx(bgrSrc, dst, MORPH_TOPHAT, element)
            return dst
        }

        fun blackHat(src: Mat, selectedType: Int, kernelSize: Int) : Mat {
            val bgrSrc = gray2bgr(src)
            val element = getElement(selectedType, kernelSize)
            val dst = Mat()
            morphologyEx(bgrSrc, dst, MORPH_BLACKHAT, element)
            return dst
        }

        fun horizontalLineExtract(src: Mat, vertical: Int) : Mat {

            val gray = bgr2gray(src)

            val bw = Mat()

            Core.bitwise_not(gray, gray)
            adaptiveThreshold(gray, bw, 255.0, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15,
                (-2).toDouble()
            )

            val result = bw.clone()

            if (vertical == 0) {
                val verticalSize = result.rows() / 30
                val verticalStructure = getStructuringElement(MORPH_RECT, Size(1.0, verticalSize.toDouble()))
                erode(result, result, verticalStructure)
                dilate(result, result, verticalStructure)
            } else {
                val horizontalSize = result.cols() / 30
                val horizontalStructure = getStructuringElement(MORPH_RECT, Size(horizontalSize.toDouble(), 1.0))
                erode(result, result, horizontalStructure)
                dilate(result, result, horizontalStructure)
            }

            Core.bitwise_not(result, result)
            val edges = Mat()
            adaptiveThreshold(result, edges, 255.0, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 3, -2.0)

            val kernel = Mat.ones(2, 2, CvType.CV_8UC1)
            dilate(edges, edges, kernel)

            val smooth = Mat()
            result.copyTo(smooth)

            blur(smooth, smooth, Size(2.0,2.0))
            smooth.copyTo(result, edges)

            return result
        }

        fun basicThreshold(src: Mat, threshold: Int, type: Int) : Mat {
            val gray = bgr2gray(src)
            val dst = Mat()
            threshold(gray, dst, threshold.toDouble(), 255.0, type)
            return dst
        }

        fun sobelDerivatives(src: Mat) : Mat {
            val bgrSrc = gray2bgr(src)
            GaussianBlur(bgrSrc, bgrSrc, Size(3.0,3.0), 0.0, 0.0, Core.BORDER_DEFAULT)

            val gray = bgr2gray(bgrSrc)

            val grad_x = Mat()
            val grad_y = Mat()
            val abs_grad_x = Mat()
            val abs_grad_y = Mat()

            Sobel(gray, grad_x, CvType.CV_16S, 1, 0, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
            Sobel(gray, grad_y, CvType.CV_16S, 0, 1, 3, 1.0, 0.0, Core.BORDER_DEFAULT)

            Core.convertScaleAbs(grad_x, abs_grad_x)
            Core.convertScaleAbs(grad_y, abs_grad_y)

            val grad = Mat()
            Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0.0, grad)
            return grad
        }

        fun laplaceOperator(src: Mat) : Mat {
            val bgrSrc = gray2bgr(src)
            GaussianBlur(bgrSrc, bgrSrc, Size(3.0,3.0), 0.0, 0.0, Core.BORDER_DEFAULT)

            val gray = bgr2gray(bgrSrc)

            val absDst = Mat()
            val dst = Mat()
            Laplacian(gray, dst, CvType.CV_16S, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
            Core.convertScaleAbs(dst, absDst)
            return absDst
        }

        fun histogramCalculation(src: Mat) : Mat {
            val bgrSrc = gray2bgr(src)

            val bgrPlanes = mutableListOf<Mat>()
            Core.split(bgrSrc, bgrPlanes)

            val histSize = 256

            val range = floatArrayOf(0f, 256f) //the upper boundary is exclusive
            val histRange = MatOfFloat(*range)

            val accumulate = false

            val bHist = Mat()
            val gHist = Mat()
            val rHist = Mat()
            calcHist(bgrPlanes, MatOfInt(0), Mat(), bHist, MatOfInt(histSize), histRange, accumulate)
            calcHist(bgrPlanes, MatOfInt(1), Mat(), gHist, MatOfInt(histSize), histRange, accumulate)
            calcHist(bgrPlanes, MatOfInt(2), Mat(), rHist, MatOfInt(histSize), histRange, accumulate)

            val histW = 512
            val histH  = 400
            val binW = Math.round((histW / histSize).toDouble())

            val histImage = Mat(histH, histW, CvType.CV_8UC3, Scalar(0.0,0.0,0.0))

            Core.normalize(bHist, bHist, 0.0, histImage.rows().toDouble(), Core.NORM_MINMAX)
            Core.normalize(gHist, gHist, 0.0, histImage.rows().toDouble(), Core.NORM_MINMAX)
            Core.normalize(rHist, rHist, 0.0, histImage.rows().toDouble(), Core.NORM_MINMAX)

            val bHistData = FloatArray((bHist.total() * bHist.channels()).toInt())
            bHist.get(0, 0, bHistData)
            val gHistData = FloatArray((gHist.total() * gHist.channels()).toInt())
            gHist.get(0, 0, gHistData)
            val rHistData = FloatArray((rHist.total() * rHist.channels()).toInt())
            rHist.get(0, 0, rHistData)

            for (i in 1 until histSize) {
                line(
                    histImage, Point((binW * (i - 1)).toDouble(),
                        (histH - Math.round(bHistData[i - 1])).toDouble()
                    ),
                    Point((binW * i).toDouble(), (histH - Math.round(bHistData[i])).toDouble()), Scalar(255.0, 0.0, 0.0), 2
                )
                line(
                    histImage, Point((binW * (i - 1)).toDouble(),
                        (histH - Math.round(gHistData[i - 1])).toDouble()
                    ),
                    Point((binW * i).toDouble(), (histH - Math.round(gHistData[i])).toDouble()), Scalar(0.0, 255.0, 0.0), 2
                )
                line(
                    histImage, Point((binW * (i - 1)).toDouble(),
                        (histH - Math.round(rHistData[i - 1])).toDouble()
                    ),
                    Point((binW * i).toDouble(), (histH - Math.round(rHistData[i])).toDouble()), Scalar(0.0, 0.0, 255.0), 2
                )
            }

            return histImage
        }

        fun convexHull(src: Mat, threshold: Int) : Mat {
            val rng = Random(12345)
            val gray = bgr2gray(src)

            blur(gray, gray, Size(3.0,3.0))

            val cannyOutput = Mat()
            Canny(gray, cannyOutput, threshold.toDouble(), (threshold * 2).toDouble())
            val contours = mutableListOf<MatOfPoint>()
            val hierarchy = Mat()
            findContours(
                cannyOutput,
                contours,
                hierarchy,
                RETR_TREE,
                CHAIN_APPROX_SIMPLE
            )
            val hullList = mutableListOf<MatOfPoint>()
            for (contour in contours) {
                val hull = MatOfInt()
                convexHull(contour, hull)
                val contourArray = contour.toArray()
                val hullPoints = arrayOfNulls<Point>(hull.rows())
                val hullContourIdxList = hull.toList()
                for (i in hullContourIdxList.indices) {
                    hullPoints[i] = contourArray[hullContourIdxList[i]]
                }
                hullList.add(MatOfPoint(*hullPoints))
            }
            val drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3)
            for (i in contours.indices) {
                val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(),
                    rng.nextInt(256).toDouble()
                )
                drawContours(drawing, contours, i, color)
                drawContours(drawing, hullList, i, color)
            }

            return drawing
        }

        fun cannyEdgeDetector(src: Mat, threshold: Int) : Mat {
            val gray = bgr2gray(src)
            val srcBlur = Mat()
            val detectedEdges = Mat()
            blur(gray, srcBlur, Size(3.0,3.0))
            Canny(srcBlur, detectedEdges, threshold.toDouble(), (threshold * 3).toDouble(), 3, false)
            val dst = Mat(gray.size(), CvType.CV_8UC3, Scalar.all(0.0))
            gray.copyTo(dst, detectedEdges)
            return dst

        }

        fun histogramEqualization(src: Mat) : Mat {
            val gray = bgr2gray(src)
            val dst = Mat()
            equalizeHist(gray, dst)
            return dst
        }

        fun contoursFinding(src: Mat, threshold: Int) : Mat {
            val rng = Random(54321)

            val gray = bgr2gray(src)

            blur(gray, gray, Size(3.0, 3.0))

            val cannyOutput = Mat()
            Canny(gray, cannyOutput, threshold.toDouble(), (threshold * 2).toDouble())

            val contours = arrayListOf<MatOfPoint>()
            val hierarchy = Mat()

            findContours(cannyOutput, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE)

            val drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3)

            for (i in 0 until contours.size) {
                val color = Scalar(rng.nextDouble(256.0), rng.nextDouble(256.0), rng.nextDouble(256.0))
                drawContours(drawing, contours, i, color, 2, LINE_8, hierarchy, 0, Point())
            }

            return drawing
        }

        private fun bgr2gray(src: Mat) : Mat {
            var gray = Mat()

            if (src.channels() == 3) {
                cvtColor(src, gray, COLOR_BGR2GRAY)
            } else {
                gray = src
            }

            return gray
        }

        private fun gray2bgr(src: Mat) : Mat {
            var color = Mat()

            if (src.channels() == 1) {
                cvtColor(src, color, COLOR_GRAY2BGR)
            } else {
                color = src
            }

            return color
        }

    }
}