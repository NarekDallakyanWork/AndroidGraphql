package bazaar.tech.com.network.retrofit

import bazaar.tech.com.constant.Network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient private constructor() {
    private lateinit var retrofit: Retrofit
    val api: ApiService
        get() = retrofit.create(ApiService::class.java)
    private var BASE_URL = Network.BASE_URL_BACKEND

    companion object {
        private var mInstance: RetrofitClient? = null
        @get:Synchronized
        val instance: RetrofitClient?
            get() {
                if (mInstance == null) {
                    mInstance =
                        RetrofitClient()
                }
                return mInstance
            }
    }

    fun baseUrl(baseUrl: String): RetrofitClient {
        BASE_URL = baseUrl
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return this
    }
}