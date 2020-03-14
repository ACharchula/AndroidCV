package pl.antonic.androidcv.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SingleServerProvider : ServerProvider {

    private val BASE_URL = "http://192.168.1.17:8080/" //"http://10.0.2.2:8080/"

    companion object {
        var retrofit: Retrofit? = null
    }

    override fun getRetrofitClient(): Retrofit {
        if(retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        return retrofit!!
    }
}