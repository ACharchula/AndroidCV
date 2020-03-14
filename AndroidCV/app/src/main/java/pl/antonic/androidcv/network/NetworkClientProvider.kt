package pl.antonic.androidcv.network

import retrofit2.Retrofit

class NetworkClientProvider {

    companion object {
        var provider : ServerProvider = SingleServerProvider()

        fun getNetworkClient() : Retrofit{
            return this.provider.getRetrofitClient()
        }
    }
}