package pl.antonic.androidcv.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_view_video.*
import pl.antonic.androidcv.processing.ProcessingTask
import pl.antonic.androidcv.R
import pl.antonic.androidcv.presenters.ViewVideoActivityPresenter

class ViewVideoActivity : AppCompatActivity(), ViewVideoActivityPresenter.View {

    private lateinit var presenter: ViewVideoActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_video)

        presenter = ViewVideoActivityPresenter(this)
        loading()

        prevButton.setOnClickListener {
            presenter.previousVideo()
        }

        nextButton.setOnClickListener {
            presenter.nextVideo()
        }

        playButton.setOnClickListener {
            presenter.play()
        }

        presenter.setProcessingTasks(intent.getSerializableExtra("processingTasks") as ArrayList<ProcessingTask>)
        presenter.startProcessing(getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.cancelAndCleanUp()
    }

    override fun setProcessingTime(text: String) {
        videoProcessingTime.text = text
    }

    override fun stopPlayback() {
        videoView.stopPlayback()
    }

    override fun setPathAndStart(path: String) {
        videoView.setVideoPath(path)
        videoView.start()
    }

    override fun showToast(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun loading() {
        progressBar.visibility = VISIBLE
        videoProcessingTime.visibility = INVISIBLE
        videoLayout.visibility = INVISIBLE
        videoView.visibility = INVISIBLE
        playButton.visibility = INVISIBLE
        nextButton.visibility = INVISIBLE
        prevButton.visibility = INVISIBLE
    }

    override fun showResults(amount: Int) {
        progressBar.visibility = INVISIBLE
        videoProcessingTime.visibility = VISIBLE

        if (amount > 0) {
            videoLayout.visibility = VISIBLE
            videoView.visibility = VISIBLE
            playButton.visibility = VISIBLE
        }

        if (amount > 1) {
            nextButton.visibility = VISIBLE
            prevButton.visibility = VISIBLE
        }
    }
}
