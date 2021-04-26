package bazaar.tech.com.product.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductByCategoryViewModel : ViewModel() {

    private val productByCategoryRepository = ProductByCategoryRepository()

    fun getProduct(categoryHandle: String) {

        productByCategoryRepository.productByCategoryLiveData = MutableLiveData()
        productByCategoryRepository.getProduct(categoryHandle)
    }

    fun observePagedLiveData() = productByCategoryRepository.pagedListLiveData

    fun observeProductByCategoryStateLiveData() =
        productByCategoryRepository.productByCategoryLiveData
}