package bazaar.tech.com.base.recyclerview

import ithd.bazaar.app.R


open class ViewType(
    var type: Type
) {

    var success: Boolean = false
    var message: String = ""

    enum class Type {
        CATEGORIES,
        PRODUCT_BY_CATEGORIES,
        BASKET,
        ERROR,
        EMPTY,
        FOOD,
        ON_ITEM_FRONT_LOADED
    }

    companion object {

        fun viewHolderLayout(viewType: Int): Int {

            return when (viewType) {
                Type.CATEGORIES.ordinal -> {
                    R.layout.vh_categories
                }
                Type.PRODUCT_BY_CATEGORIES.ordinal -> {
                    R.layout.vh_sub_product
                }
                Type.BASKET.ordinal -> {
                    R.layout.vh_bascket
                }
                else -> 0
            }

        }
    }
}