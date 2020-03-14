package pl.antonic.androidcv.processing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.antonic.androidcv.methods.ImageProcessingMethodWrapper
import pl.antonic.androidcv.network.NetworkClientProvider
import pl.antonic.androidcv.network.UploadAPIs
import pl.antonic.androidcv.presenters.ViewPictureActivityPresenter
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class PhotoServerProcessing(
    private val filePath: String,
    private val methodsWrapper: ImageProcessingMethodWrapper,
    private val idx: Int,
    var presenter: ViewPictureActivityPresenter
) : AsyncTask<Void, ProcessingExceptions, Void>() {

    var bitmap: Bitmap? = null

    override fun doInBackground(vararg p0: Void?): Void? {
        process(this.filePath, this.methodsWrapper)
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        presenter.setBitmapAndDecrement(idx, bitmap)
    }

    private fun process(filePath: String, methodsWrapper: ImageProcessingMethodWrapper) {
        val retrofit = NetworkClientProvider.getNetworkClient()
        val uploadAPIs = retrofit.create(UploadAPIs::class.java)
        val file = File(Uri.parse(filePath).path)

        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

        val json = Gson().toJson(methodsWrapper)
        val jsonRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), json)

        val call = uploadAPIs.processImage(filePart, jsonRequestBody)

        try {
            val fileByteArray = call.execute().body()!!.bytes()
            this.bitmap = BitmapFactory.decodeByteArray(fileByteArray, 0, fileByteArray.size)
        } catch (e : ConnectException) {
            bitmap = null
            publishProgress(ProcessingExceptions.CONNECTION_FAILED)

        } catch (e : SocketTimeoutException) {
            bitmap = null
            publishProgress(ProcessingExceptions.TIMEOUT)
        } catch (e: IOException) {
            bitmap = null
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