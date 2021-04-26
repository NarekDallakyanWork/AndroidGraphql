package bazaar.tech.com.categories.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bazaar.tech.com.categories.model.CategoriesResponseModel

class CategoriesViewModel : ViewModel() {

    var categoriesLiveData = MutableLiveData<CategoriesResponseModel>()
    private val categoriesRepository =
        CategoriesRepository()

    fun getCategories() {

        categoriesRepository.categoriesLiveData = categoriesLiveData
        categoriesRepository.getCategories()
    }
}