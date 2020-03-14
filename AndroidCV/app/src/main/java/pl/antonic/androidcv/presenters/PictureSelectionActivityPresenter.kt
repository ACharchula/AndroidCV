package pl.antonic.androidcv.presenters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.FileOutputStream


class PictureSelectionActivityPresenter(private val view: View) {

    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_GET_GALLERY_PHOTO = 2
    val REQUEST_GET_VIDEO = 3

    private var videoProcessing = false
    private var displayedPictureIndex = 0
    private var selectedPhotos = arrayListOf<String>()
    private var tempFile : String? = null

    fun isVideoProcessing() : Boolean {
        return videoProcessing
    }

    fun getPaths() : ArrayList<String> {
        return selectedPhotos
    }

    fun setTempFile(path : String) {
        tempFile = path
    }

    fun processActivityResult(resultCode: Int, requestCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> showTakenPhoto()
                REQUEST_GET_GALLERY_PHOTO -> showSelectedPhoto(data)
                REQUEST_GET_VIDEO -> showSelectedVideo(data)
            }
        }
    }

    private fun showTakenPhoto() {
        selectedPhotos.add(tempFile!!)
        tempFile = null
        rotateImageIfNecessary(0)
        view.setImageView(Uri.parse(selectedPhotos[0]))
        view.picturePreviewView(selectedPhotos.size)
        videoProcessing = false
    }

    private fun showSelectedVideo(data: Intent?) {
        getSelectedItems(data)
        view.setVideoViewAndStart(selectedPhotos[0])
        view.videoPreviewView(selectedPhotos.size)
        videoProcessing = true
    }

    private fun showSelectedPhoto(data: Intent?) {
        getSelectedItems(data)

        for (index in 0 until selectedPhotos.size) {
            rotateImageIfNecessary(index)
        }
        view.setImageView(Uri.parse(selectedPhotos[0]))
        view.picturePreviewView(selectedPhotos.size)
        videoProcessing = false
    }

    private fun getSelectedItems(data: Intent?) {
        val clipData = data?.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val videoItem = clipData.getItemAt(i)
                val videoURI = videoItem.uri
                val filePath = getPath(view.getContext(), videoURI)
                selectedPhotos.add(filePath)
            }
        } else {
            val videoURI = data!!.data!!
            val filePath = getPath(view.getContext(), videoURI)
            selectedPhotos.add(filePath)
        }
    }

    private fun getPath(context: Context, uri: Uri) : String {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]

            var contentUri : Uri? = null

            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = mutableListOf(split[1])

            var cursor : Cursor? = null
            val column = "_data"
            val projection = mutableListOf(column)

            try {
                cursor = context.contentResolver.query(contentUri!!,
                    projection.toTypedArray(), selection, selectionArgs.toTypedArray(), null)

                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } finally {
                cursor?.close()
            }

        }

        return ""
    }

    private fun rotateImageIfNecessary(index: Int) {
        val bitmap = BitmapFactory.decodeFile(selectedPhotos[index])
        val ei = ExifInterface(selectedPhotos[index])
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        var rotatedBitmap : Bitmap? = null

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90.0F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180.0F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270.0F)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
            ExifInterface.ORIENTATION_UNDEFINED -> rotatedBitmap = bitmap
        }

        if (orientation != ExifInterface.ORIENTATION_NORMAL && orientation != ExifInterface.ORIENTATION_UNDEFINED) {
            val out = FileOutputStream(selectedPhotos[index])
            rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun reload() {
        selectedPhotos.clear()
        videoProcessing = false
        tempFile = null
        displayedPictureIndex = 0
        view.startView()
    }

    fun goToPictureGallery() : Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        return intent
    }

    fun goToVideoGallery() : Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "video/*"
        return intent
    }

    fun displayNext() {
        if (!videoProcessing) {
            if (displayedPictureIndex < selectedPhotos.size - 1) {
                displayedPictureIndex++
                view.setImageView(Uri.parse(selectedPhotos[displayedPictureIndex]))
            }
        } else {
            if (displayedPictureIndex < selectedPhotos.size - 1) {
                displayedPictureIndex++
                view.stopVideoPlayback()
                view.setVideoViewAndStart(selectedPhotos[displayedPictureIndex])
            }
        }
    }

    fun displayPrevious() {
        if (!videoProcessing) {
            if (displayedPictureIndex > 0) {
                displayedPictureIndex--
                view.setImageView(Uri.parse(selectedPhotos[displayedPictureIndex]))
            }
        } else {
            if (displayedPictureIndex > 0) {
                displayedPictureIndex--
                view.stopVideoPlayback()
                view.setVideoViewAndStart(selectedPhotos[displayedPictureIndex])
            }
        }
    }

    interface View {
        fun startView()
        fun picturePreviewView(amount : Int)
        fun videoPreviewView(amount : Int)
        fun setImageView(uri: Uri)
        fun setVideoViewAndStart(path : String)
        fun stopVideoPlayback()
        fun getContext() : Context
    }
}