package bazaar.tech.com.network.apollo.client

import com.apollographql.apollo.ApolloClient
import bazaar.tech.com.constant.Apollo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit

class ApolloClient {
    companion object {

        private const val BASE_URL = "https://my-bazaar-pk.myshopify.com/api/2020-01/graphql.json"

        private val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(NetworkInterceptor())
                .build()
        }


        val apolloClient: ApolloClient by lazy {
            ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(httpClient)
                .build()
        }

        private class NetworkInterceptor : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(
                    chain.request().newBuilder()
                        .header("X-Shopify-Storefront-Access-Token", Apollo.ACCESS_TOKEN).build()
                )
            }
        }

    }
}