package bazaar.tech.com.basket.view.viewholder

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import bazaar.tech.com.AppApplication
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.basket.model.BasketModel
import ithd.bazaar.app.R
import kotlinx.android.synthetic.main.vh_bascket.view.*

class BasketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var onClick: ((ViewType) -> Unit?)? = null

    fun onBind(
        data: BasketModel?,
        position: Int = -1,
        onClick: ((ViewType) -> Unit?)?
    ) {
        this.onClick = onClick
        if (position % 2 != 0) {
            itemView.basketItemRoot.setBackgroundColor(
                ContextCompat.getColor(
                    AppApplication.appContext,
                    R.color.recycler_gray_bg
                )
            )
        }

        itemView.basketQuantity.text = data?.quantity.toString()
        itemView.basketTitle.text = data?.title
        itemView.basketDescription.text = data?.description
        itemView.basketPrice.text =
            itemView.context.getString(R.string.currency).plus(" " + getRoundPrice(data?.price))

        itemView.basketImage.setCornerRadiiDP(10f, 10f, 10f, 10f)
        if (!TextUtils.isEmpty(data?.imageUrl) && data?.imageUrl != "null") {
            itemView.basketImage.load(data?.imageUrl)
        } else{
            itemView.basketImage.setBackgroundResource(R.drawable.product_placeholder)
        }

        itemView.basketItem.setOnClickListener {
            if (onClick != null)
                onClick(data!!)
        }
    }

    private fun getRoundPrice(price: String?): String {
        if (TextUtils.isEmpty(price))
            return "0"

        if (price?.startsWith("0")!!)
            return "0"


        if (price.contains(".")) {

            val dotIndex = price.indexOf('.')
            return price.substring(0, dotIndex)
        }
        return price
    }
}