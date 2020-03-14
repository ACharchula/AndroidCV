package pl.antonic.androidcv.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import pl.antonic.androidcv.R
import pl.antonic.androidcv.network.NetworkClientProvider
import pl.antonic.androidcv.network.SingleServerProvider
import pl.antonic.androidcv.network.UploadAPIs
import retrofit2.Call
import retrofit2.Response
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 200)
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), 300)
        }

        initOpenCV()

        startButton.setOnClickListener {
            val intent = Intent(this, PictureSelectionActivity::class.java)
            startActivity(intent)
        }

        testButton.setOnClickListener {
            testConnection()
        }
    }

    private fun initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private fun testConnection() {
        val retrofit = NetworkClientProvider.getNetworkClient()
        val uploadAPIs = retrofit.create(UploadAPIs::class.java)
        val call = uploadAPIs.testConnection()

        val start = System.currentTimeMillis()

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val msg = "Connect exception!"
                val toast = Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT)
                toast.show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val end = System.currentTimeMillis()
                val result = (end - start).toDouble() / 1000
                val speed = round(10 / result * 100) / 100
                val msg = "10 MB has been downloaded in $result seconds. It's about $speed MB/s."
                val toast = Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG)
                toast.show()
            }

        })
    }
}
