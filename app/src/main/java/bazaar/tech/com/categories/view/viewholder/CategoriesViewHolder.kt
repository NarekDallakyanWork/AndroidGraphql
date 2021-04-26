package bazaar.tech.com.categories.view.viewholder

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import bazaar.tech.com.AppApplication
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.categories.model.CategoriesResponseModel
import ithd.bazaar.app.R
import kotlinx.android.synthetic.main.vh_categories.view.*

class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var onClick: ((ViewType) -> Unit?)? = null

    fun onBind(
        data: CategoriesResponseModel.CategoryModel?,
        position: Int = -1,
        onClick: ((ViewType) -> Unit?)?
    ) {

        this.onClick = onClick
        itemView.categoriesTitle.text = data?.title
        if (position % 2 != 0) {
            itemView.categoryRootItem.setBackgroundColor(
                ContextCompat.getColor(
                    AppApplication.appContext,
                    R.color.recycler_gray_bg
                )
            )
        }
        itemView.categoriesImage.setCornerRadiiDP(10f, 10f, 10f, 10f)
        if (!TextUtils.isEmpty(data?.imageUrl) && data?.imageUrl != "null") {
            itemView.categoriesImage.load(data?.imageUrl)
        } else {
            itemView.categoriesImage.setBackgroundResource(R.drawable.categories_placeholder)
        }

        itemView.categoryItem.setOnClickListener {
            if (onClick != null)
                onClick(data!!)
        }
    }
}