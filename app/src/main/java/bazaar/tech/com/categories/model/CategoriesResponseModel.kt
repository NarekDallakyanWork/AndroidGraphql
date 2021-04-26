package bazaar.tech.com.categories.model

import bazaar.tech.com.auth.model.State
import bazaar.tech.com.base.recyclerview.ViewType

class CategoriesResponseModel {

    var categories: ArrayList<ViewType?>? = null
    var state: State? = null

    class CategoryModel(
        var id: String?,
        var title: String?,
        var imageUrl: String?,
        var handle: String?
    ) : ViewType(Type.CATEGORIES)
}

enum class State {
    IN_PROGRESS,
    FAILURE,
    SUCCESS
}