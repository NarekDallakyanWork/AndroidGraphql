package bazaar.tech.com.basket.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CheckoutLastListQuery
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient

class BasketStateRepository {

    lateinit var basketStateLiveData: MutableLiveData<Boolean?>

    fun checkBasketItemCount() {
        val accessToken = SharedHelper.getKey("accessToken")

        if (accessToken == null)
            basketStateLiveData.value = false


        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    basketStateLiveData.postValue(null)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        basketStateLiveData.postValue(null)
                        return
                    }

                    val incompleteCheckout =
                        response
                            .data()
                            ?.customer()
                            ?.lastIncompleteCheckout()

                    if (incompleteCheckout != null) {

                        val lineItems: List<CheckoutLastListQuery.Edge?>? =
                            incompleteCheckout.lineItems().edges()

                        if (lineItems == null) {
                            SharedHelper.putKey("basketAmount", "")
                            basketStateLiveData.postValue(false)
                            return
                        }

                        if (lineItems.isEmpty()) {
                            SharedHelper.putKey("basketAmount", "")
                            basketStateLiveData.postValue(false)
                            return
                        }

                        SharedHelper.putKey("basketAmount", lineItems.size.toString())
                        basketStateLiveData.postValue(true)

                    } else {
                        SharedHelper.putKey("basketAmount", "")
                        basketStateLiveData.postValue(false)
                    }
                }
            })
    }

}