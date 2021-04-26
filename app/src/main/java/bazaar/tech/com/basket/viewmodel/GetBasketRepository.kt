package bazaar.tech.com.basket.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CheckoutLastListQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.basket.mapper.fromApolloBasketListToBasketModelList
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient

class GetBasketRepository {

    lateinit var basketLiveData: MutableLiveData<ArrayList<ViewType?>>


    fun getBasket() {
        val accessToken = SharedHelper.getKey("accessToken")

        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    basketLiveData.postValue(null)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        basketLiveData.postValue(null)
                        return
                    }

                    val basket =
                        response.data()?.customer()?.lastIncompleteCheckout()?.lineItems()?.edges()

                    if (basket == null) {
                        basketLiveData.postValue(ArrayList())
                        return
                    }

                    basketLiveData.postValue(
                        fromApolloBasketListToBasketModelList(
                            basket
                        )
                    )
                }
            })
    }
}