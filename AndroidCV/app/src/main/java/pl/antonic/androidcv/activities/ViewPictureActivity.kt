package pl.antonic.androidcv.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_view_picture.*
import kotlin.collections.ArrayList
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.AsyncTask
import com.github.chrisbanes.photoview.PhotoView
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import pl.antonic.androidcv.*
import pl.antonic.androidcv.presenters.ViewPictureActivityPresenter
import pl.antonic.androidcv.processing.PhotoClientProcessing
import pl.antonic.androidcv.processing.ProcessingExceptions
import pl.antonic.androidcv.processing.ProcessingTask
import pl.antonic.androidcv.processing.PhotoServerProcessing

class ViewPictureActivity : AppCompatActivity(), ViewPictureActivityPresenter.View {

    private val imageViews = mutableListOf<ImageView>()
    private lateinit var presenter: ViewPictureActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_picture)
        loading()

        presenter = ViewPictureActivityPresenter(this)
        presenter.setProcessingTasks(intent.getSerializableExtra("processingTasks") as ArrayList<ProcessingTask>)
        presenter.startProcessing()
    }

    override fun setImageViewAtIndex(idx: Int, bitmap: Bitmap) {
        imageViews[idx].setImageBitmap(bitmap)
    }

    override fun setProcessingTime(text: String) {
        processingTime.text = text
    }

    override fun loading() {
        processingTime.visibility = INVISIBLE
        scrollView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
    }

    override fun showResults() {
        processingTime.visibility = VISIBLE
        scrollView.visibility = VISIBLE
        progressBar.visibility = INVISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.cancelAllTasks()
    }

    override fun showToast(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun createAndAddImageView() {
        val imageView = ImageView(this)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.adjustViewBounds = true

        imageView.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this)
            val mView = layoutInflater.inflate(R.layout.zoomable_picture_dialog, null)
            val photoView = mView.findViewById<PhotoView>(R.id.zoomablePicture)
            photoView.setImageBitmap(imageView.drawable.toBitmap())
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        imagesLinearLayout.addView(imageView)

        imageViews.add(imageView)
    }
}
