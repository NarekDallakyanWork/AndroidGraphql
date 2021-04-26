package bazaar.tech.com.product.model

import android.os.Parcelable
import bazaar.tech.com.base.recyclerview.ViewType
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductModel(
    var id: String?,
    var title: String?,
    var imageUrl: String?,
    var description: String?,
    var price: String?,
    var variantId: String,
    var tag: String?,
    var nextPageReference: String?
) : ViewType(Type.PRODUCT_BY_CATEGORIES), Parcelable