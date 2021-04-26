package bazaar.tech.com.basket.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CheckoutLastListQuery
import com.example.UpdateCheckoutForIDMutation
import com.example.type.CheckoutLineItemInput
import bazaar.tech.com.basket.model.CheckoutModel
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient

class ClearBasketRepository {

    lateinit var clearBasketLiveData: MutableLiveData<Boolean?>

    fun clearBasket() {
        val accessToken = SharedHelper.getKey("accessToken")

        if (accessToken == null)
            clearBasketLiveData.value = false


        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    clearBasketLiveData.postValue(false)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        clearBasketLiveData.postValue(false)
                        return
                    }

                    val checkoutId = response.data()?.customer()?.lastIncompleteCheckout()

                    if (checkoutId != null) {
                        getUserCheckouts(checkoutId.id())
                    } else {
                        clearBasketLiveData.postValue(true)
                    }
                }
            })
    }

    private fun getUserCheckouts(checkoutId: String) {

        val accessToken = SharedHelper.getKey("accessToken")

        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    clearBasketLiveData.postValue(false)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        clearBasketLiveData.postValue(false)
                        return
                    }

                    val checkoutList = ArrayList<CheckoutModel>()
                    val checkouts =
                        response.data()?.customer()?.lastIncompleteCheckout()?.lineItems()?.edges()

                    if (checkouts == null) {
                        clearBasketLiveData.postValue(false)
                        return
                    }
                    updateToEmptyCheckoutList(checkoutId)
                }
            })
    }

    private fun updateToEmptyCheckoutList(checkoutId: String) {

        val lineItemList = ArrayList<CheckoutLineItemInput>()

        val updateCheckoutMutation = UpdateCheckoutForIDMutation.builder()
            .id(checkoutId)
            .lineItems(lineItemList)
            .build()

        ApolloClient
            .apolloClient
            .mutate(updateCheckoutMutation)
            .enqueue(object : ApolloCall.Callback<UpdateCheckoutForIDMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    clearBasketLiveData.postValue(false)
                }

                override fun onResponse(response: Response<UpdateCheckoutForIDMutation.Data>) {
                    if (response.hasErrors()) {
                        clearBasketLiveData.postValue(false)
                        return
                    }
                    SharedHelper.putKey("basketAmount", "")
                    clearBasketLiveData.postValue(true)
                }
            })
    }
}