package pl.antonic.androidcv.network

import retrofit2.Retrofit

interface ServerProvider {

    fun getRetrofitClient() : Retrofit
}