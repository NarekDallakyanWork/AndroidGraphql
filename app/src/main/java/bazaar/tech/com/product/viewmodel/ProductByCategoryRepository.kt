package bazaar.tech.com.product.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.FirstProductByCollectionHandleQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.network.apollo.client.ApolloClient
import bazaar.tech.com.product.viewmodel.datasource.ProductDataSourceFactory

class ProductByCategoryRepository {

    // liveData
    private var bazaarDataSourceFactory =
        ProductDataSourceFactory<FirstProductByCollectionHandleQuery.Data>()
    lateinit var pagedListLiveData: LiveData<PagedList<ViewType>>
    var productByCategoryLiveData: MutableLiveData<ViewType.Type>? = null


    fun getProduct(categoryHandle: String) {
        val productQuery = FirstProductByCollectionHandleQuery.builder()
            .first(10)
            .handle(categoryHandle)
            .build()

        val apolloQueryCall = ApolloClient
            .apolloClient
            .query(productQuery)
        bazaarDataSourceFactory.addApolloCall(
            apolloQueryCall,
            categoryHandle = categoryHandle,
            onError = {
                handleOnError(it)
            })

        this.pagedListLiveData = LivePagedListBuilder(
            bazaarDataSourceFactory,
            10
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<ViewType>() {

            override fun onZeroItemsLoaded() {
                super.onZeroItemsLoaded()
                println()
            }

            override fun onItemAtEndLoaded(itemAtEnd: ViewType) {
                super.onItemAtEndLoaded(itemAtEnd)
                println()
            }

            override fun onItemAtFrontLoaded(itemAtFront: ViewType) {
                super.onItemAtFrontLoaded(itemAtFront)
                productByCategoryLiveData?.postValue(ViewType.Type.ON_ITEM_FRONT_LOADED)
            }
        }).build()
    }

    private fun handleOnError(message: String) {
        if (message.isEmpty()) {
            productByCategoryLiveData?.postValue(ViewType.Type.EMPTY)
        } else{
            productByCategoryLiveData?.let {
                productByCategoryLiveData?.postValue(ViewType.Type.ERROR)
            }
        }
    }

}