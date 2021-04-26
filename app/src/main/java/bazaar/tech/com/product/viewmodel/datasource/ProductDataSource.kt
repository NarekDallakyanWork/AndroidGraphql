package bazaar.tech.com.product.viewmodel.datasource

import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.ProductByCollectionHandleQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.product.mapper.toApolloQuery
import bazaar.tech.com.product.mapper.toViewTypeList

class ProductDataSource<INITIAL> : PageKeyedDataSource<Int, ViewType>() {

    var onError: ((String) -> Unit?)? = null

    lateinit var apolloCallInitial: ApolloQueryCall<INITIAL>

    // Product by collection
    var categoryHandle: String? = null
    var productByCollectionApolloCallMore: ApolloQueryCall<ProductByCollectionHandleQuery.Data>? =
        null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ViewType>
    ) {

        apolloCallInitial
            .enqueue(object : ApolloCall.Callback<INITIAL>() {
                override fun onFailure(e: ApolloException) {
                    e.message?.let { onError?.let { it1 -> it1(it) } }
                }

                override fun onResponse(response: Response<INITIAL>) {
                    val toViewTypeList =
                        toViewTypeList(response)

                    if (toViewTypeList.isNotEmpty()) {
                        toApolloQuery(
                            toViewTypeList,
                            handle = categoryHandle
                        )?.let {
                            @Suppress("UNCHECKED_CAST")
                            productByCollectionApolloCallMore =
                                it as ApolloQueryCall<ProductByCollectionHandleQuery.Data>
                            callback.onResult(toViewTypeList, null, 1)
                        }
                    } else {
                        onError?.let { it("") }
                    }
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ViewType>) {

        if (productByCollectionApolloCallMore != null)
            productByCollectionApolloCallMore
                ?.enqueue(object : ApolloCall.Callback<ProductByCollectionHandleQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        e.message?.let {
                            onError?.let { it1 -> it1(it) }
                        }
                    }

                    override fun onResponse(response: Response<ProductByCollectionHandleQuery.Data>) {
                        val toViewTypeList =
                            toViewTypeList(
                                response
                            )
                        if (toViewTypeList.isEmpty()) return

                        toApolloQuery(
                            toViewTypeList,
                            handle = categoryHandle
                        )?.let {
                            @Suppress("UNCHECKED_CAST")
                            productByCollectionApolloCallMore =
                                it as ApolloQueryCall<ProductByCollectionHandleQuery.Data>
                            callback.onResult(toViewTypeList, params.key + 1)
                        }
                    }
                })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ViewType>) {

        if (productByCollectionApolloCallMore != null)
            productByCollectionApolloCallMore
                ?.enqueue(object : ApolloCall.Callback<ProductByCollectionHandleQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        e.message?.let { onError?.let { it1 -> it1(it) } }
                    }

                    override fun onResponse(response: Response<ProductByCollectionHandleQuery.Data>) {
                        val toViewTypeList =
                            toViewTypeList(
                                response
                            )
                        if (toViewTypeList.isEmpty()) return

                        toApolloQuery(
                            toViewTypeList,
                            handle = categoryHandle
                        )?.let {
                            @Suppress("UNCHECKED_CAST")
                            productByCollectionApolloCallMore =
                                it as ApolloQueryCall<ProductByCollectionHandleQuery.Data>
                            callback.onResult(toViewTypeList, params.key - 1)
                        }
                    }
                })
    }
}