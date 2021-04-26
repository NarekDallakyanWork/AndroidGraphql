package bazaar.tech.com.product.viewmodel.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloQueryCall
import bazaar.tech.com.base.recyclerview.ViewType

class ProductDataSourceFactory<INITIAL> :
    DataSource.Factory<Int, ViewType>() {

    var onError: ((String) -> Unit?)? = null
    private val itemLiveDataSource =
        MutableLiveData<PageKeyedDataSource<Int, ViewType>>()
    private var apolloCallInitial: ApolloQueryCall<INITIAL>? = null
    private var categoryHandle: String? = null
    private var itemDataSource: ProductDataSource<INITIAL>? = null

    fun addApolloCall(
        apolloCall: ApolloQueryCall<INITIAL>,
        categoryHandle: String? = "",
        onError: ((String) -> Unit?)? = null
    ) {
        this.apolloCallInitial = apolloCall
        this.categoryHandle = categoryHandle
        this.onError = onError
    }

    override fun create(): DataSource<Int, ViewType> {
        itemDataSource =
            ProductDataSource()

        if (apolloCallInitial == null)
            throw NullPointerException("ApolloCall<T> can't be null")
        if (itemDataSource == null)
            throw NullPointerException("BazaarDataSource<INITIAL> can't be null")

        itemDataSource?.apolloCallInitial = apolloCallInitial as ApolloQueryCall<INITIAL>
        itemDataSource?.categoryHandle = categoryHandle.toString()
        itemDataSource?.onError = onError

        itemLiveDataSource.postValue(itemDataSource)
        return itemDataSource!!
    }
}