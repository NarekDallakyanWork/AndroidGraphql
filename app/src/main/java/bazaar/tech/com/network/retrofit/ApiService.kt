package bazaar.tech.com.network.retrofit

import bazaar.tech.com.auth.model.LoginGetEmailResponse
import bazaar.tech.com.auth.model.PhoneModel
import bazaar.tech.com.basket.model.OrderModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("email")
    suspend fun getEmailByPhoneNumber(@Body phoneModel: PhoneModel): Response<LoginGetEmailResponse>

    @POST("create_order")
    suspend fun createOrder(@Body orderModel: OrderModel): Response<Any>
}