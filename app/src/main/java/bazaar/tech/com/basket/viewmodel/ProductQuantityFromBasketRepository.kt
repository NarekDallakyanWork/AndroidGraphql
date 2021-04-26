package bazaar.tech.com.basket.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CheckoutLastListQuery
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient
import bazaar.tech.com.product.model.ProductModel

class ProductQuantityFromBasketRepository {

    lateinit var productQuantityFromBasket: MutableLiveData<Int>

    fun getProductQuantityFromBasket(productModel: ProductModel) {
        val accessToken = SharedHelper.getKey("accessToken")

        if (accessToken == null)
            productQuantityFromBasket.value = 1


        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    productQuantityFromBasket.postValue(1)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        productQuantityFromBasket.postValue(1)
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
                            productQuantityFromBasket.postValue(1)
                            return
                        }

                        if (lineItems.isEmpty()) {
                            productQuantityFromBasket.postValue(1)
                            return
                        }


                        var quantity = 1;
                        for (edge in lineItems) {

                            if (edge?.node()?.variant()?.id() == productModel.variantId) {
                                quantity = edge.node().quantity()
                                break
                            }
                        }

                        productQuantityFromBasket.postValue(quantity)

                    } else {
                        productQuantityFromBasket.postValue(1)

                    }
                }
            })
    }

}