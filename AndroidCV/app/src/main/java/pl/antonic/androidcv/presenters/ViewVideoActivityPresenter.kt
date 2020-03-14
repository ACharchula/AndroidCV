package pl.antonic.androidcv.presenters

import android.os.AsyncTask
import pl.antonic.androidcv.processing.ProcessingExceptions
import pl.antonic.androidcv.processing.ProcessingTask
import pl.antonic.androidcv.processing.VideoServerProcessing
import java.io.File

class ViewVideoActivityPresenter(private val view: View) {

    private var start: Long = 0
    private var end: Long = 0
    private var tasksAmount = 0
    private var processedVideos = mutableListOf<String>()
    private var displayedVideoIndex = 0
    private var asyncTasks = mutableListOf<AsyncTask<Void, ProcessingExceptions, Void>>()
    private lateinit var processingTasks : ArrayList<ProcessingTask>

    fun startProcessing(fileDir: File) {
        tasksAmount = processingTasks.size
        start = System.currentTimeMillis()

        for (task in processingTasks) {
            val asyncTask =
                VideoServerProcessing(task.path, task.methodWrapper, fileDir, this)
            asyncTasks.add(asyncTask)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    @Synchronized
    fun addVideo(processedVideoPath: String) {
        tasksAmount--

        if (processedVideoPath != "") {
            processedVideos.add(processedVideoPath)
        }

        if (tasksAmount == 0) {
            view.showResults(processedVideos.size)
            end = System.currentTimeMillis()
            view.setProcessingTime("Processing time = " + (end - start).toDouble() / 1000)

            if (processedVideos.size > 0)
                view.setPathAndStart(processedVideos[0])
        }
    }

    fun previousVideo() {
        if (displayedVideoIndex > 0) {
            view.stopPlayback()
            displayedVideoIndex--
            view.setPathAndStart(processedVideos[displayedVideoIndex])
        }
    }

    fun nextVideo() {
        if (displayedVideoIndex < processedVideos.size - 1) {
            view.stopPlayback()
            displayedVideoIndex++
            view.setPathAndStart(processedVideos[displayedVideoIndex])
        }
    }

    fun play() {
        view.stopPlayback()
        view.setPathAndStart(processedVideos[displayedVideoIndex])
    }

    fun cancelAndCleanUp() {
        for (task in asyncTasks) {
            task.cancel(true)
        }

        for (video in processedVideos) {
            File(video).delete()
        }
    }

    fun showToast(msg: String) {
        view.showToast(msg)
    }

    fun setProcessingTasks(processingTasks: ArrayList<ProcessingTask>) {
        this.processingTasks = processingTasks
    }

    interface View {
        fun stopPlayback()
        fun setPathAndStart(path: String)
        fun loading()
        fun showResults(amount: Int)
        fun setProcessingTime(text: String)
        fun showToast(msg: String)
    }
}