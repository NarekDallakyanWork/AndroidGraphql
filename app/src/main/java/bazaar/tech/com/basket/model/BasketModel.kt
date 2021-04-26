package bazaar.tech.com.basket.model

import bazaar.tech.com.base.recyclerview.ViewType

class BasketModel(
    var quantity: Int?,
    var price: String?,
    var variantId: String?,
    var imageUrl: String?,
    var title: String?,
    var description: String?
) : ViewType(Type.BASKET)