package pl.antonic.androidcv.presenters

import android.graphics.Bitmap
import android.os.AsyncTask
import pl.antonic.androidcv.processing.PhotoClientProcessing
import pl.antonic.androidcv.processing.PhotoServerProcessing
import pl.antonic.androidcv.processing.ProcessingExceptions
import pl.antonic.androidcv.processing.ProcessingTask

class ViewPictureActivityPresenter(private val view: View) {

    private var asyncTasks = mutableListOf<AsyncTask<Void, ProcessingExceptions, Void>>()
    private lateinit var processingTasks : ArrayList<ProcessingTask>

    private var start : Long = 0
    private var end : Long = 0
    private var tasksAmount = 0

    fun setProcessingTasks(processingTasks: ArrayList<ProcessingTask>) {
        this.processingTasks = processingTasks
    }

    fun startProcessing() {
        tasksAmount = processingTasks.size
        start = System.currentTimeMillis()

        for (idx in 0 until processingTasks.size) {
            view.createAndAddImageView()

            val task = processingTasks[idx]
            var asyncTask : AsyncTask<Void, ProcessingExceptions, Void>

            asyncTask = if (task.serverProcessing) {
                PhotoServerProcessing(task.path, task.methodWrapper, idx, this)
            } else {
                PhotoClientProcessing(task.path, task.methodWrapper, idx, this)
            }

            asyncTasks.add(asyncTask)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

        }
    }

    @Synchronized
    fun setBitmapAndDecrement(idx: Int, bitmap: Bitmap?) {
        if(bitmap != null)
            view.setImageViewAtIndex(idx, bitmap)

        tasksAmount--

        if (tasksAmount == 0) {
            view.showResults()
            end = System.currentTimeMillis()
            view.setProcessingTime("Processing time = " + (end - start).toDouble() / 1000)
        }
    }

    fun showToast(msg: String) {
        view.showToast(msg)
    }

    fun cancelAllTasks() {
        for (task in asyncTasks) {
            task.cancel(true)
        }
    }

    interface View {
        fun createAndAddImageView()
        fun loading()
        fun showResults()
        fun setImageViewAtIndex(idx: Int, bitmap: Bitmap)
        fun setProcessingTime(text: String)
        fun showToast(msg: String)
    }
}