package pl.antonic.androidcv.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu.nextButton
import kotlinx.android.synthetic.main.activity_menu.prevButton
import pl.antonic.androidcv.R
import pl.antonic.androidcv.methods.ImageProcessingMethod
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.methods.Methods
import pl.antonic.androidcv.presenters.MenuActivityPresenter
import pl.antonic.androidcv.processing.ProcessingTask

class MenuActivity : AppCompatActivity(), MenuActivityPresenter.View {

    private lateinit var presenter : MenuActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        presenter = MenuActivityPresenter(this)
        presenter.setVideoProcessing(intent.getBooleanExtra("video", false))
        presenter.setPaths(intent.getSerializableExtra("photoPaths") as ArrayList<String>)

        processButton.setOnClickListener {
            saveChoices()
            processImage()
        }

        nextButton.setOnClickListener {
            presenter.displayNext()
        }

        prevButton.setOnClickListener {
            presenter.displayPrevious()
        }

        setCheckboxesListeners()
        setSeekBarsListeners()
    }

    override fun setNextAndPrevButtonsInvisible() {
        prevButton.visibility = INVISIBLE
        nextButton.visibility = INVISIBLE
    }

    override fun setImageView(bitmap: Bitmap) {
        currentPictureImageView.setImageBitmap(bitmap)
    }

    override fun setVideoMode() {
        serverSwitch.isChecked = true
        serverSwitch.setOnClickListener {
            serverSwitch.isChecked = true
            val duration = Toast.LENGTH_SHORT
            val msg = "Video processing available only on server"
            val toast = Toast.makeText(applicationContext, msg, duration)
            toast.show()
        }
    }

    private fun setCheckboxesListeners() {
        erosionCheckbox.setOnClickListener {
            if (erosionCheckbox.isChecked)
                erosionSettings.visibility = VISIBLE
            else
                erosionSettings.visibility = GONE
        }

        gaussianSmoothingCheckbox.setOnClickListener {
            if (gaussianSmoothingCheckbox.isChecked)
                gaussianSmoothingSettings.visibility = VISIBLE
            else
                gaussianSmoothingSettings.visibility = GONE
        }

        convexHullCheckbox.setOnClickListener {
            if (convexHullCheckbox.isChecked)
                convexHullSettings.visibility = VISIBLE
            else
                convexHullSettings.visibility = GONE
        }

        cannyEdgeDetectorCheckbox.setOnClickListener {
            if (cannyEdgeDetectorCheckbox.isChecked)
                cannyEdgeDetectorSettings.visibility = VISIBLE
            else
                cannyEdgeDetectorSettings.visibility = GONE
        }

        smoothingCheckbox.setOnClickListener {
            if (smoothingCheckbox.isChecked)
                bilateralFilterSettings.visibility = VISIBLE
            else
                bilateralFilterSettings.visibility = GONE
        }

        dilationCheckbox.setOnClickListener {
            if (dilationCheckbox.isChecked)
                dilationSettings.visibility = VISIBLE
            else
                dilationSettings.visibility = GONE
        }

        openingCheckbox.setOnClickListener {
            if (openingCheckbox.isChecked)
                openingSettings.visibility = VISIBLE
            else
                openingSettings.visibility = GONE
        }

        closingCheckbox.setOnClickListener {
            if (closingCheckbox.isChecked)
                closingSettings.visibility = VISIBLE
            else
                closingSettings.visibility = GONE
        }

        blackHatCheckbox.setOnClickListener {
            if (blackHatCheckbox.isChecked)
                blackHatSettings.visibility = VISIBLE
            else
                blackHatSettings.visibility = GONE
        }

        gradientCheckbox.setOnClickListener {
            if (gradientCheckbox.isChecked)
                gradientSettings.visibility = VISIBLE
            else
                gradientSettings.visibility = GONE
        }

        topHatCheckbox.setOnClickListener {
            if (topHatCheckbox.isChecked)
                topHatSettings.visibility = VISIBLE
            else
                topHatSettings.visibility = GONE
        }

        basicThresholdCheckbox.setOnClickListener {
            if (basicThresholdCheckbox.isChecked)
                basicThresholdSettings.visibility = VISIBLE
            else
                basicThresholdSettings.visibility = GONE
        }

        lineExtractCheckbox.setOnClickListener {
            if (lineExtractCheckbox.isChecked)
                lineExtractSettings.visibility = VISIBLE
            else
                lineExtractSettings.visibility = GONE
        }

        contoursFindingCheckbox.setOnClickListener {
            if (contoursFindingCheckbox.isChecked)
                contoursFindingSettings.visibility = VISIBLE
            else
                contoursFindingSettings.visibility = GONE
        }
    }

    private fun setSeekBarsListeners() {
        gaussianSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressGaussianSmoothingSeekBar.text = gaussianSeekBar.progress.toString()}
        })

        convexHullSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressConvexHullSeekBar.text = convexHullSeekBar.progress.toString()}
        })

        cannyEdgeDetectorSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressCannyEdgeDetector.text = cannyEdgeDetectorSeekBar.progress.toString()}
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressSmoothingSeekBar.text = seekBar.progress.toString()}
        })

        erosionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressErosion.text = erosionSeekBar.progress.toString()            }
        })

        dilationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressDilation.text = dilationSeekBar.progress.toString()            }
        })

        openingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressOpening.text = openingSeekBar.progress.toString()            }
        })

        closingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressClosing.text = closingSeekBar.progress.toString()            }
        })

        blackHatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressBlackHat.text = blackHatSeekBar.progress.toString()            }
        })

        gradientSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressGradient.text = gradientSeekBar.progress.toString()            }
        })

        topHatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressTopHat.text = topHatSeekBar.progress.toString()            }
        })

        basicThresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressBasicTreshold.text = basicThresholdSeekBar.progress.toString()            }
        })

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressContoursFinding.text = seekBar2.progress.toString()            }
        })
    }

    //order of saving the methods is vital, image will be processed later in the same order
    override fun saveChoices() {
        val methodWrapper = ImageProcessingMethodWrapper()

        if (histogramEqCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.HISTOGRAM_EQUALIZATION, listOf()))

        if (gaussianSmoothingCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.GAUSSIAN_BLUR, listOf(gaussianSeekBar.progress.toString())))

        if (smoothingCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.BILATERAL_FILTER, listOf(seekBar.progress.toString())))

        if (erosionCheckbox.isChecked) {
            var type = 0
            when {
                erosionRectButton.isChecked -> type = 0
                erosionCrossButton.isChecked -> type = 1
                erosionElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.EROSION, listOf(erosionSeekBar.progress.toString(), type.toString())))
        }

        if (dilationCheckbox.isChecked) {
            var type = 0
            when {
                dilationRectButton.isChecked -> type = 0
                dilationCrossButton.isChecked -> type = 1
                dilationElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.DILATION, listOf(dilationSeekBar.progress.toString(), type.toString())))
        }

        if (openingCheckbox.isChecked) {
            var type = 0
            when {
                openingRectButton.isChecked -> type = 0
                openingCrossButton.isChecked -> type = 1
                openingElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.OPENING, listOf(openingSeekBar.progress.toString(), type.toString())))
        }

        if (closingCheckbox.isChecked) {
            var type = 0
            when {
                closingRectButton.isChecked -> type = 0
                closingCrossButton.isChecked -> type = 1
                closingElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.CLOSING, listOf(closingSeekBar.progress.toString(), type.toString())))
        }

        if (blackHatCheckbox.isChecked) {
            var type = 0
            when {
                blackHatRectButton.isChecked -> type = 0
                blackHatCrossButton.isChecked -> type = 1
                blackHatElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.BLACK_HAT, listOf(blackHatSeekBar.progress.toString(), type.toString())))
        }

        if (gradientCheckbox.isChecked) {
            var type = 0
            when {
                gradientRectButton.isChecked -> type = 0
                gradientCrossButton.isChecked -> type = 1
                gradientElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.GRADIENT, listOf(gradientSeekBar.progress.toString(), type.toString())))
        }

        if (topHatCheckbox.isChecked) {
            var type = 0
            when {
                topHatRectButton.isChecked -> type = 0
                topHatCrossButton.isChecked -> type = 1
                topHatElipseButton.isChecked -> type = 2
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.TOP_HAT, listOf(topHatSeekBar.progress.toString(), type.toString())))
        }

        if (basicThresholdCheckbox.isChecked) {
            var type = 0

            when {
                basicThresholdBinaryButton.isChecked -> type = 0
                basicThresholdBinaryInvertedButton.isChecked -> type = 1
                basicThresholdTruncateButton.isChecked -> type = 2
                basicThresholdToZeroButton.isChecked -> type = 3
                basicThresholdToZeroInvertedButton.isChecked -> type = 4
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.BASIC_THRESHOLD, listOf(basicThresholdSeekBar.progress.toString(), type.toString())))
        }

        if (lineExtractCheckbox.isChecked) {
            var vertical = 0

            if (lineExtractSwitch.isChecked) {
                vertical = 1
            }

            methodWrapper.methods.add(ImageProcessingMethod(Methods.LINE_EXTRACT, listOf(vertical.toString())))
        }

        if (contoursFindingCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.CONTOURS_FINDING, listOf(seekBar2.progress.toString())))

        if (sobelDerivativesCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.SOBEL_DERIVATIVES, listOf()))

        if (laplaceOperatorCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.LAPLACE_OPERATOR, listOf()))

        if (convexHullCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.CONVEX_HULL, listOf(convexHullSeekBar.progress.toString())))

        if (cannyEdgeDetectorCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.CANNY_EDGE_DETECTOR, listOf(cannyEdgeDetectorSeekBar.progress.toString())))

        if (histogramCalculationCheckbox.isChecked)
            methodWrapper.methods.add(ImageProcessingMethod(Methods.HISTOGRAM_CALCULATION, listOf()))

        presenter.setProcessingTask(methodWrapper, serverSwitch.isChecked)
    }

    override fun loadChoices(processingTask : ProcessingTask) {
        serverSwitch.isChecked = processingTask.serverProcessing

        if (processingTask.methodWrapper.containsId(Methods.BILATERAL_FILTER)) {
            bilateralFilterSettings.visibility = VISIBLE
            smoothingCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.BILATERAL_FILTER)
            seekBar.progress = method!!.arguments[0].toInt()
        } else {
            bilateralFilterSettings.visibility = GONE
            smoothingCheckbox.isChecked = false
            seekBar.progress = 0
        }

        if (processingTask.methodWrapper.containsId(Methods.CONTOURS_FINDING)) {
            contoursFindingSettings.visibility = VISIBLE
            contoursFindingCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.CONTOURS_FINDING)
            seekBar2.progress = method!!.arguments[0].toInt()
        } else {
            contoursFindingSettings.visibility = GONE
            contoursFindingCheckbox.isChecked = false
            seekBar2.progress = 0
        }

        if (processingTask.methodWrapper.containsId(Methods.GAUSSIAN_BLUR)) {
            gaussianSmoothingSettings.visibility = VISIBLE
            gaussianSmoothingCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.GAUSSIAN_BLUR)
            gaussianSeekBar.progress = method!!.arguments[0].toInt()
        } else {
            gaussianSmoothingSettings.visibility = GONE
            gaussianSmoothingCheckbox.isChecked = false
            gaussianSeekBar.progress = 0
        }

        if (processingTask.methodWrapper.containsId(Methods.CONVEX_HULL)) {
            convexHullSettings.visibility = VISIBLE
            convexHullCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.CONVEX_HULL)
            convexHullSeekBar.progress = method!!.arguments[0].toInt()
        } else {
            convexHullSettings.visibility = GONE
            convexHullCheckbox.isChecked = false
            convexHullSeekBar.progress = 0
        }

        if (processingTask.methodWrapper.containsId(Methods.CANNY_EDGE_DETECTOR)) {
            cannyEdgeDetectorSettings.visibility = VISIBLE
            cannyEdgeDetectorCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.CANNY_EDGE_DETECTOR)
            cannyEdgeDetectorSeekBar.progress = method!!.arguments[0].toInt()
        } else {
            cannyEdgeDetectorSettings.visibility = GONE
            cannyEdgeDetectorCheckbox.isChecked = false
            cannyEdgeDetectorSeekBar.progress = 0
        }

        if (processingTask.methodWrapper.containsId(Methods.EROSION)) {
            erosionSettings.visibility = VISIBLE
            erosionCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.EROSION)
            erosionSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> erosionRectButton.isChecked = true
                1 -> erosionCrossButton.isChecked = true
                2 -> erosionElipseButton.isChecked = true
            }
        } else {
            erosionSettings.visibility = GONE
            erosionCheckbox.isChecked = false
            erosionSeekBar.progress = 0
            erosionRectButton.isChecked = true
            erosionCrossButton.isChecked = false
            erosionElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.DILATION)) {
            dilationSettings.visibility = VISIBLE
            dilationCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.DILATION)
            dilationSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> dilationRectButton.isChecked = true
                1 -> dilationCrossButton.isChecked = true
                2 -> dilationElipseButton.isChecked = true
            }
        } else {
            dilationSettings.visibility = GONE
            dilationCheckbox.isChecked = false
            dilationSeekBar.progress = 0
            dilationRectButton.isChecked = true
            dilationCrossButton.isChecked = false
            dilationElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.OPENING)) {
            openingSettings.visibility = VISIBLE
            openingCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.OPENING)
            openingSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when {
                type == 0 -> openingRectButton.isChecked = true
                type == 1 -> openingCrossButton.isChecked = true
                type == 2 -> openingElipseButton.isChecked = true
            }
        } else {
            openingSettings.visibility = GONE
            openingCheckbox.isChecked = false
            openingSeekBar.progress = 0
            openingRectButton.isChecked = true
            openingCrossButton.isChecked = false
            openingElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.CLOSING)) {
            closingSettings.visibility = VISIBLE
            closingCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.CLOSING)
            closingSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when {
                type == 0 -> closingRectButton.isChecked = true
                type == 1 -> closingCrossButton.isChecked = true
                type == 2 ->closingElipseButton.isChecked = true
            }
        } else {
            closingSettings.visibility = GONE
            closingCheckbox.isChecked = false
            closingSeekBar.progress = 0
            closingRectButton.isChecked = true
            closingCrossButton.isChecked = false
            closingElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.BLACK_HAT)) {
            blackHatSettings.visibility = VISIBLE
            blackHatCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.BLACK_HAT)
            blackHatSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> blackHatRectButton.isChecked = true
                1 -> blackHatCrossButton.isChecked = true
                2 -> blackHatElipseButton.isChecked = true
            }
        } else {
            blackHatSettings.visibility = GONE
            blackHatCheckbox.isChecked = false
            blackHatSeekBar.progress = 0
            blackHatRectButton.isChecked = true
            blackHatCrossButton.isChecked = false
            blackHatElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.GRADIENT)) {
            gradientSettings.visibility = VISIBLE
            gradientCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.GRADIENT)
            gradientSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> gradientRectButton.isChecked = true
                1 -> gradientCrossButton.isChecked = true
                2 -> gradientElipseButton.isChecked = true
            }
        } else {
            gradientSettings.visibility = GONE
            gradientCheckbox.isChecked = false
            gradientSeekBar.progress = 0
            gradientRectButton.isChecked = true
            gradientCrossButton.isChecked = false
            gradientElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.TOP_HAT)) {
            topHatSettings.visibility = VISIBLE
            topHatCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.TOP_HAT)
            topHatSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> topHatRectButton.isChecked = true
                1 -> topHatCrossButton.isChecked = true
                2 -> topHatElipseButton.isChecked = true
            }
        } else {
            topHatSettings.visibility = GONE
            topHatCheckbox.isChecked = false
            topHatSeekBar.progress = 0
            topHatRectButton.isChecked = true
            topHatCrossButton.isChecked = false
            topHatElipseButton.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.LINE_EXTRACT)) {
            lineExtractSettings.visibility = VISIBLE
            lineExtractCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.LINE_EXTRACT)
            lineExtractSwitch.isChecked = method!!.arguments[0].toInt() == 1
        } else {
            lineExtractSettings.visibility = GONE
            lineExtractCheckbox.isChecked = false
            lineExtractSwitch.isChecked = false
        }

        if (processingTask.methodWrapper.containsId(Methods.BASIC_THRESHOLD)) {
            basicThresholdSettings.visibility = VISIBLE
            basicThresholdCheckbox.isChecked = true
            val method = processingTask.methodWrapper.getMethod(Methods.BASIC_THRESHOLD)
            basicThresholdSeekBar.progress = method!!.arguments[0].toInt()
            val type = method.arguments[1].toInt()
            when (type) {
                0 -> basicThresholdBinaryButton.isChecked = true
                1 -> basicThresholdBinaryInvertedButton.isChecked = true
                2 -> basicThresholdTruncateButton.isChecked = true
                3 -> basicThresholdToZeroButton.isChecked = true
                4 -> basicThresholdToZeroInvertedButton.isChecked = true
            }
        } else {
            basicThresholdSettings.visibility = GONE
            basicThresholdCheckbox.isChecked = false
            basicThresholdSeekBar.progress = 0
            basicThresholdBinaryButton.isChecked = true
            basicThresholdBinaryInvertedButton.isChecked = false
            basicThresholdTruncateButton.isChecked = false
            basicThresholdToZeroButton.isChecked = false
            basicThresholdToZeroInvertedButton.isChecked = false
        }

        histogramEqCheckbox.isChecked = processingTask.methodWrapper.containsId(Methods.HISTOGRAM_EQUALIZATION)
        histogramCalculationCheckbox.isChecked = processingTask.methodWrapper.containsId(Methods.HISTOGRAM_CALCULATION)
        sobelDerivativesCheckbox.isChecked = processingTask.methodWrapper.containsId(Methods.SOBEL_DERIVATIVES)
        laplaceOperatorCheckbox.isChecked = processingTask.methodWrapper.containsId(Methods.LAPLACE_OPERATOR)
    }

    private fun processImage() {
        val viewIntent : Intent = if (presenter.isVideoProcessing()) {
            Intent(this, ViewVideoActivity::class.java)
        } else {
            Intent(this, ViewPictureActivity::class.java)
        }

        viewIntent.putExtra("processingTasks", presenter.getProcessingTasks())
        startActivity(viewIntent)
    }
}
