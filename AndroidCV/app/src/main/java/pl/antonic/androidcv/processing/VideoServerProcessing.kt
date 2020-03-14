package pl.antonic.androidcv.processing

import android.net.Uri
import android.os.AsyncTask
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.network.NetworkClientProvider
import pl.antonic.androidcv.network.UploadAPIs
import pl.antonic.androidcv.presenters.ViewVideoActivityPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class VideoServerProcessing(
    private val filePath: String,
    private val methodsWrapper: ImageProcessingMethodWrapper,
    private val root: File,
    private val presenter: ViewVideoActivityPresenter
) : AsyncTask<Void, ProcessingExceptions, Void>() {

    private lateinit var processedVideo : String

    override fun doInBackground(vararg p0: Void?): Void? {
        process(this.filePath, this.methodsWrapper, root)
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        presenter.addVideo(processedVideo)
    }

    private fun process(filePath: String, methodsWrapper: ImageProcessingMethodWrapper, root: File) {
        val retrofit = NetworkClientProvider.getNetworkClient()
        val uploadAPIs = retrofit.create(UploadAPIs::class.java)
        val file = File(Uri.parse(filePath).path)

        val fileRequestBody = RequestBody.create(MediaType.parse("video/*"), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

        val json = Gson().toJson(methodsWrapper)
        val jsonRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), json)

        val call = uploadAPIs.processVideo(filePart, jsonRequestBody)

        try {
            val fileByteArray = call.execute().body()!!.bytes()
            this.processedVideo = root.absolutePath + System.currentTimeMillis().toString() + ".mp4"
            val videoFile = File(processedVideo)
            val out = FileOutputStream(videoFile)
            out.write(fileByteArray)
            out.close()
        } catch (e : ConnectException) {
            processedVideo = ""
            publishProgress(ProcessingExceptions.CONNECTION_FAILED)
        } catch (e : SocketTimeoutException) {
            processedVideo = ""
            publishProgress(ProcessingExceptions.TIMEOUT)
        } catch (e: IOException) {
            processedVideo = ""
            publishProgress(ProcessingExceptions.CONNECTION_FAILED)
        }
    }

    override fun onProgressUpdate(vararg exception: ProcessingExceptions?) {
        var msg = "Unknown error"

        if (exception[0] == ProcessingExceptions.TIMEOUT) {
            msg = "Processing timeout has been reached!"
        } else if (exception[0] == ProcessingExceptions.CONNECTION_FAILED) {
            msg = "Connection to server failed!"
        }

        presenter.showToast(msg)
    }
}