package bazaar.tech.com.categories.viewmodel

import com.example.CategoriesQuery
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.categories.model.CategoriesResponseModel

fun fromEdgesToCategoriesResponseModel(edges: MutableList<CategoriesQuery.Edge>): CategoriesResponseModel {
    val categoriesResponseModel = CategoriesResponseModel()
    edges.also {
        val categoryList = ArrayList<ViewType?>()
        for (item in it) {
            var imageUrl: String? = null
            item.node().image()?.let { image ->
                imageUrl = image.originalSrc().toString()
            }
            categoryList.add(
                CategoriesResponseModel.CategoryModel(
                    item.node().id(),
                    item.node().title(),
                    imageUrl,
                    item.node().handle()
                )
            )
        }
        categoriesResponseModel.categories = categoryList
    }
    return categoriesResponseModel
}