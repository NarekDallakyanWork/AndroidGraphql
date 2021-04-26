package bazaar.tech.com.basket.mapper

import com.example.CheckoutLastListQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.basket.model.BasketModel

fun fromApolloBasketListToBasketModelList(basket: List<CheckoutLastListQuery.Edge>): ArrayList<ViewType?>? {

    val basketList = ArrayList<ViewType?>()
    for (basketItem in basket) {

        basketList.add(
            BasketModel(
                basketItem.node().quantity(),
                basketItem.node().variant()?.price().toString(),
                basketItem.node().variant()?.id(),
                basketItem.node().variant()?.image()?.originalSrc().toString(),
                basketItem.node().title(),
                ""
            )
        )

    }
    return basketList
}