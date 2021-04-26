package bazaar.tech.com.product.mapper

import android.text.TextUtils
import com.apollographql.apollo.api.Response
import com.example.FirstProductByCollectionHandleQuery
import com.example.ProductByCollectionHandleQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.network.apollo.client.ApolloClient
import bazaar.tech.com.product.model.ProductModel

fun <APOLLO_RESPONSE> toViewTypeList(response: Response<APOLLO_RESPONSE>): List<ViewType> {

    var viewTypeList: List<ViewType> = ArrayList()
    if (response.data() is FirstProductByCollectionHandleQuery.Data) {
        viewTypeList =
            fromDataToFirstProductByCollectionHandleQuery(
                response
            )
    }else if (response.data() is ProductByCollectionHandleQuery.Data) {
        viewTypeList =
            fromDataToProductByCollectionHandleQuery(
                response
            )
    }

    return viewTypeList
}

fun toApolloQuery(
    viewTypeList: List<ViewType>,
    handle: String? = null
): Any? {

    if (viewTypeList[0].type == ViewType.Type.PRODUCT_BY_CATEGORIES) {

        if (TextUtils.isEmpty(handle)) return null

        val nextPageReference =
            (viewTypeList[viewTypeList.size - 1] as ProductModel).nextPageReference
        val productQuery = ProductByCollectionHandleQuery.builder()
            .first(3)
            .after(nextPageReference!!)
            .handle(handle!!)
            .build()

        return ApolloClient
            .apolloClient
            .query(productQuery)

    }
    return null
}

private fun <APOLLO_RESPONSE> fromDataToFirstProductByCollectionHandleQuery(response: Response<APOLLO_RESPONSE>)
        : List<ViewType> {

    val productByCategoryModelList = ArrayList<ProductModel>()
    @Suppress("DEPRECATION") val edges =
        (response.data() as FirstProductByCollectionHandleQuery.Data)
            .shop()
            .collectionByHandle()
            ?.products()
            ?.edges()

    edges?.let {

        for (edge in edges) {
            val id = edge.node().id()
            val title = edge.node().title()
            val description = edge.node().description()
            val price: String = edge.node().variants().edges()[0].node().price().toString()
            val variantId: String = edge.node().variants().edges()[0].node().id()
            var imageUrl: String? = null
            if (edge.node().images().edges().size > 0) {
                imageUrl = edge.node().images().edges()[0]?.node()?.originalSrc().toString()
            }
            val cursor = edge.cursor()
            productByCategoryModelList.add(
                ProductModel(
                    id,
                    title,
                    imageUrl.toString(),
                    description,
                    price,
                    variantId,
                    title,
                    cursor
                )
            )
        }
    }
    return productByCategoryModelList
}

private fun <APOLLO_RESPONSE> fromDataToProductByCollectionHandleQuery(response: Response<APOLLO_RESPONSE>)
        : List<ViewType> {

    val productByCategoryModelList = ArrayList<ProductModel>()
    @Suppress("DEPRECATION") val edges =
        (response.data() as ProductByCollectionHandleQuery.Data)
            .shop()
            .collectionByHandle()
            ?.products()
            ?.edges()

    edges?.let {

        for (edge in edges) {
            val id = edge.node().id()
            val title = edge.node().title()
            val description = edge.node().description()
            val price: String = edge.node().variants().edges()[0].node().price().toString()
            val variantId: String = edge.node().variants().edges()[0].node().id()
            var imageUrl: String? = null
            if (edge.node().images().edges().size > 0) {
                imageUrl = edge.node().images().edges()[0]?.node()?.originalSrc().toString()
            }

            val cursor = edge.cursor()
            productByCategoryModelList.add(
                ProductModel(
                    id,
                    title,
                    imageUrl.toString(),
                    description,
                    price,
                    variantId,
                    title,
                    cursor
                )
            )
        }
    }
    return productByCategoryModelList
}

