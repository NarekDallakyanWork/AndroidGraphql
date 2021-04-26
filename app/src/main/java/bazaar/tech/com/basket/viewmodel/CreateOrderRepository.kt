package bazaar.tech.com.basket.viewmodel

import bazaar.tech.com.basket.model.OrderModel
import bazaar.tech.com.constant.Network
import bazaar.tech.com.network.retrofit.RetrofitClient

class CreateOrderRepository {

    suspend fun createOrder(orderModel: OrderModel) =
        RetrofitClient.instance?.baseUrl(Network.BASE_URL_BACKEND)?.api?.createOrder(
            orderModel
        )
}