package pl.antonic.androidcv.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadAPIs {

    @Multipart
    @POST("/process_image")
    fun processImage(@Part file: MultipartBody.Part, @Part("json") json: RequestBody) : Call<ResponseBody>

    @Multipart
    @POST("/process_video")
    fun processVideo(@Part file: MultipartBody.Part, @Part("json") json: RequestBody) : Call<ResponseBody>

    @GET("/test_connection")
    fun testConnection() : Call<ResponseBody>
}