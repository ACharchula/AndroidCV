package pl.antonic.androidcv.presenters

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.processing.ProcessingTask
import pl.antonic.androidcv.utilities.LargePictureHandler

class MenuActivityPresenter(private val view : View){

    private var currentPictureIndex = 0
    private lateinit var paths : ArrayList<String>
    private val smallerBitmaps = mutableListOf<Bitmap>()
    private val processingTasks = arrayListOf<ProcessingTask>()
    private var videoProcessing = false

    fun setPaths(paths : ArrayList<String>) {
        this.paths = paths

        if (this.paths.size == 1) {
            view.setNextAndPrevButtonsInvisible()
        }

        prepareThumbnailsAndProcessingTasks()
        view.setImageView(smallerBitmaps[0])
    }

    fun setVideoProcessing(isVideoProcessing : Boolean) {
        videoProcessing = isVideoProcessing

        if (videoProcessing) {
            view.setVideoMode()
        }
    }

    fun isVideoProcessing() : Boolean {
        return videoProcessing
    }

    fun getProcessingTasks() : ArrayList<ProcessingTask> {
        return processingTasks
    }

    private fun prepareThumbnailsAndProcessingTasks() {
        for (photoPath in paths) {
            val processingTask = ProcessingTask(photoPath)
            if (videoProcessing) {
                processingTask.serverProcessing = true
                smallerBitmaps.add(ThumbnailUtils.createVideoThumbnail(photoPath, MediaStore.Images.Thumbnails.MINI_KIND))
            } else {
                smallerBitmaps.add(LargePictureHandler.decodeSampledBitmapFromResource(photoPath, 400, 400))
            }
            processingTasks.add(processingTask)
        }
    }

    fun displayNext() {
        if (currentPictureIndex < processingTasks.size - 1) {
            view.saveChoices()
            currentPictureIndex++
            view.setImageView(smallerBitmaps[currentPictureIndex])
            view.loadChoices(processingTasks[currentPictureIndex])
        }
    }

    fun displayPrevious() {
        if (currentPictureIndex > 0) {
            view.saveChoices()
            currentPictureIndex--
            view.setImageView(smallerBitmaps[currentPictureIndex])
            view.loadChoices(processingTasks[currentPictureIndex])
        }
    }

    fun setProcessingTask(methodWrapper: ImageProcessingMethodWrapper, serverProcessing: Boolean) {
        processingTasks[currentPictureIndex].serverProcessing = serverProcessing
        processingTasks[currentPictureIndex].methodWrapper = methodWrapper
    }

    interface View {
        fun setNextAndPrevButtonsInvisible()
        fun setVideoMode()
        fun setImageView(bitmap: Bitmap)
        fun saveChoices()
        fun loadChoices(processingTask: ProcessingTask)
    }
}