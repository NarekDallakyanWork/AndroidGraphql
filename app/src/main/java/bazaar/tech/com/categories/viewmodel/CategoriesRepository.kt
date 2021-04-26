package bazaar.tech.com.categories.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CategoriesQuery
import bazaar.tech.com.auth.model.State
import bazaar.tech.com.categories.model.CategoriesResponseModel
import bazaar.tech.com.network.apollo.client.ApolloClient

class CategoriesRepository {


    lateinit var categoriesLiveData: MutableLiveData<CategoriesResponseModel>

    fun getCategories() {
        ApolloClient
            .apolloClient
            .query(CategoriesQuery.builder().build())
            .enqueue(object :
                ApolloCall.Callback<CategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    loginMessage(State.FAILURE)
                }

                override fun onResponse(response: Response<CategoriesQuery.Data>) {
                    if (!response.hasErrors()) {

                        val edges = response.data()?.collections()?.edges()
                        val categories =
                            fromEdgesToCategoriesResponseModel(
                                edges!!
                            )
                        categories.state = State.SUCCESS
                        loginMessage(State.SUCCESS, categoriesResponseModel = categories)
                    } else {
                        loginMessage(State.FAILURE)
                    }
                }
            })
    }

    private fun loginMessage(
        state: State,
        categoriesResponseModel: CategoriesResponseModel? = null
    ) {
        if (categoriesResponseModel == null) {
            val categoriesModel = CategoriesResponseModel()
            categoriesModel.state = state
            categoriesLiveData.postValue(categoriesModel)
        } else {
            categoriesLiveData.postValue(categoriesResponseModel)
        }

    }
}