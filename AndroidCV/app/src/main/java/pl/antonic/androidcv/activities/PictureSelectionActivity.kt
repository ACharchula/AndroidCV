package pl.antonic.androidcv.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_picture_selection.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import pl.antonic.androidcv.R
import pl.antonic.androidcv.presenters.PictureSelectionActivityPresenter

class PictureSelectionActivity : AppCompatActivity() , PictureSelectionActivityPresenter.View {

    private lateinit var presenter : PictureSelectionActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_selection)

        presenter = PictureSelectionActivityPresenter(this)

        takePictureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        reloadButton.setOnClickListener {
            presenter.reload()
        }

        galleryButton.setOnClickListener {
            startActivityForResult(presenter.goToPictureGallery(), presenter.REQUEST_GET_GALLERY_PHOTO)
        }

        videoButton.setOnClickListener {
            startActivityForResult(presenter.goToVideoGallery(), presenter.REQUEST_GET_VIDEO)
        }

        nextButton.setOnClickListener {
            presenter.displayNext()
        }

        prevButton.setOnClickListener {
            presenter.displayPrevious()
        }

        submitButton.setOnClickListener {
            switchToMenuView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (presenter.isVideoProcessing()) {
            selectedVideoView.stopPlayback()
            selectedVideoView.start()
        }
    }

    private fun switchToMenuView() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("photoPaths", presenter.getPaths())
        intent.putExtra("video", presenter.isVideoProcessing())
        startActivity(intent)
    }

    override fun startView() {
        galleryButton.visibility = VISIBLE
        takePictureButton.visibility = VISIBLE
        selectedPictureImageView.visibility = INVISIBLE
        submitButton.visibility = INVISIBLE
        reloadButton.visibility = INVISIBLE
        nextButton.visibility = INVISIBLE
        prevButton.visibility = INVISIBLE
        videoButton.visibility = VISIBLE
        selectedVideoView.visibility = INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.processActivityResult(resultCode, requestCode, data)
    }

    override fun getContext(): Context {
        return this
    }

    override fun stopVideoPlayback() {
        selectedVideoView.stopPlayback()
    }

    override fun picturePreviewView(amount: Int) {
        galleryButton.visibility = INVISIBLE
        takePictureButton.visibility = INVISIBLE
        selectedPictureImageView.visibility = VISIBLE
        submitButton.visibility = VISIBLE
        reloadButton.visibility = VISIBLE
        selectedVideoView.visibility = INVISIBLE

        if (amount > 1) {
            nextButton.visibility = VISIBLE
            prevButton.visibility = VISIBLE
        }

        videoButton.visibility = INVISIBLE
    }

    override fun videoPreviewView(amount: Int) {
        selectedVideoView.visibility = VISIBLE
        galleryButton.visibility = INVISIBLE
        takePictureButton.visibility = INVISIBLE
        selectedPictureImageView.visibility = INVISIBLE
        submitButton.visibility = VISIBLE
        reloadButton.visibility = VISIBLE

        if (amount > 1) {
            nextButton.visibility = VISIBLE
            prevButton.visibility = VISIBLE
        }

        videoButton.visibility = INVISIBLE
    }

    override fun setImageView(uri: Uri) {
        selectedPictureImageView.setImageURI(uri)
    }

    override fun setVideoViewAndStart(path: String) {
        selectedVideoView.setVideoPath(path)
        selectedVideoView.start()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Grant storage permissions for the app!", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "pl.antonic.androidcv.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, presenter.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            presenter.setTempFile(absolutePath)
        }
    }
}
