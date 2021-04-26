package bazaar.tech.com.basket.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CheckoutLastListQuery
import com.example.CreateCheckoutMutation
import com.example.UpdateCheckoutForIDMutation
import com.example.type.CheckoutLineItemInput
import bazaar.tech.com.basket.model.CheckoutModel
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient
import bazaar.tech.com.product.model.ProductModel

class RemoveFromBasketRepository {
    lateinit var checkoutLiveData: MutableLiveData<Boolean>

    fun removeFromBasket(quantity: Int, model: ProductModel) {
        val accessToken = SharedHelper.getKey("accessToken")

        if (accessToken == null)
            checkoutLiveData.value = false


        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    checkoutLiveData.postValue(false)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        checkoutLiveData.postValue(false)
                        return
                    }

                    val checkoutId = response.data()?.customer()?.lastIncompleteCheckout()

                    if (checkoutId != null) {
                        SharedHelper.putKey("checkoutId", checkoutId.id())
                        getUserCheckouts(quantity, model, checkoutId.id())
                    } else {
                        createCheckout(quantity, model)
                    }
                }
            })
    }

    private fun createCheckout(quantity: Int, model: ProductModel) {
        val createCheckoutMutation = CreateCheckoutMutation.builder()
            .quantity(quantity)
            .variantId(model.variantId)
            .build()


        ApolloClient
            .apolloClient
            .mutate(createCheckoutMutation)
            .enqueue(object : ApolloCall.Callback<CreateCheckoutMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    checkoutLiveData.postValue(false)
                }

                override fun onResponse(response: Response<CreateCheckoutMutation.Data>) {

                    if (response.hasErrors()) {
                        checkoutLiveData.postValue(false)
                        return
                    }
                    checkoutLiveData.postValue(true)
                }
            })
    }

    private fun getUserCheckouts(
        quantity: Int,
        model: ProductModel,
        checkoutId: String
    ) {

        val accessToken = SharedHelper.getKey("accessToken")

        val checkoutLastListQuery = CheckoutLastListQuery.builder()
            .accessToken(accessToken!!)
            .build()

        ApolloClient
            .apolloClient
            .query(checkoutLastListQuery)
            .enqueue(object : ApolloCall.Callback<CheckoutLastListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    checkoutLiveData.postValue(false)
                }

                override fun onResponse(response: Response<CheckoutLastListQuery.Data>) {

                    if (response.hasErrors()) {
                        checkoutLiveData.postValue(false)
                        return
                    }

                    val checkoutList = ArrayList<CheckoutModel>()
                    val checkouts =
                        response.data()?.customer()?.lastIncompleteCheckout()?.lineItems()?.edges()

                    if (checkouts == null) {
                        checkoutLiveData.postValue(false)
                        return
                    }

                    var checkoutIsExists = false
                    var checkoutExistingObj: CheckoutModel? = null
                    for (checkout in checkouts) {

                        checkout?.node().let {
                            val mQuantity = it?.quantity()
                            val mVariantId = it?.variant()?.id()
                            val checkoutModel =
                                CheckoutModel(
                                    mQuantity,
                                    mVariantId,
                                    model.description!!
                                )
                            if (mVariantId == model.variantId) {
                                checkoutIsExists = true
                                checkoutExistingObj = checkoutModel
                            }

                            checkoutList.add(checkoutModel)
                        }
                    }

                    if (checkoutIsExists) {
                        checkoutList.remove(checkoutExistingObj)
                        updateCheckout(checkoutId, checkoutList)
                    } else {
                        checkoutLiveData.postValue(true)
                    }
                }
            })
    }

    private fun updateCheckout(checkoutId: String, checkoutList: ArrayList<CheckoutModel>) {

        val lineItemList = ArrayList<CheckoutLineItemInput>()

        for (checkout in checkoutList) {
            val lineItem = CheckoutLineItemInput.builder()
                .quantity(checkout.quantity!!)
                .variantId(checkout.variantId!!)
                .build()
            lineItemList.add(lineItem)
        }

        val updateCheckoutMutation = UpdateCheckoutForIDMutation.builder()
            .id(checkoutId)
            .lineItems(lineItemList)
            .build()

        ApolloClient
            .apolloClient
            .mutate(updateCheckoutMutation)
            .enqueue(object : ApolloCall.Callback<UpdateCheckoutForIDMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    checkoutLiveData.postValue(false)
                }

                override fun onResponse(response: Response<UpdateCheckoutForIDMutation.Data>) {
                    if (response.hasErrors()) {
                        checkoutLiveData.postValue(false)
                        return
                    }
                    checkoutLiveData.postValue(true)
                }
            })
    }
}