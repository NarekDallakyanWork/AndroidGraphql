package bazaar.tech.com.base.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import bazaar.tech.com.basket.view.viewholder.BasketViewHolder
import bazaar.tech.com.categories.model.CategoriesResponseModel
import bazaar.tech.com.categories.view.viewholder.CategoriesViewHolder
import bazaar.tech.com.basket.model.BasketModel
import bazaar.tech.com.food.model.FoodModel
import bazaar.tech.com.product.model.ProductModel
import bazaar.tech.com.product.view.viewholder.ProductViewHolder

open class MainViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    fun onBind(data: ViewType?, position: Int = -1, onClick: ((ViewType) -> Unit?)?) {

        when (data) {
            is CategoriesResponseModel.CategoryModel -> {
                CategoriesViewHolder(itemView).onBind(data, position, onClick)
            }
            is ProductModel -> {
                ProductViewHolder(
                    itemView
                ).onBind(data, position, onClick)
            }
            is BasketModel -> {
                BasketViewHolder(itemView).onBind(data, position, onClick)
            }
        }
    }
}