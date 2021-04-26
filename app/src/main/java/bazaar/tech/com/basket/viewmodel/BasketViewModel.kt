package bazaar.tech.com.basket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.basket.model.BasketModel
import bazaar.tech.com.basket.model.OrderModel
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.product.model.ProductModel
import kotlinx.coroutines.launch

class BasketViewModel(application: Application) : AndroidViewModel(application) {

    private val addToBasketRepository =
        AddToBasketRepository()
    private val removeFromBasketRepository =
        RemoveFromBasketRepository()
    private val basketRepository =
        GetBasketRepository()
    private val basketStateRepository =
        BasketStateRepository()
    private val productQuantityFromBasketRepository =
        ProductQuantityFromBasketRepository()
    private val clearBasketRepository =
        ClearBasketRepository()
    private val createOrderRepository =
        CreateOrderRepository()

    var checkoutLiveData =  MutableLiveData<Boolean>()
    lateinit var basketStateLiveData: MutableLiveData<Boolean?>
    lateinit var createOrderLiveData: MutableLiveData<Boolean?>
    lateinit var clearBasketLiveData: MutableLiveData<Boolean?>
    lateinit var basketLiveData: MutableLiveData<ArrayList<ViewType?>>
    lateinit var productQuantityFromBasketLiveData: MutableLiveData<Int>

    fun addToBasket(
        quantity: Int,
        model: ProductModel
    ) {

        checkoutLiveData = MutableLiveData()
        addToBasketRepository.checkoutLiveData = checkoutLiveData
        addToBasketRepository.addProductToBasket(quantity, model)
    }

    fun removeFromBasket(quantity: Int, model: ProductModel) {

        checkoutLiveData = MutableLiveData()
        removeFromBasketRepository.checkoutLiveData = checkoutLiveData
        removeFromBasketRepository.removeFromBasket(quantity, model)
    }

    fun getBasket() {

        basketLiveData = MutableLiveData()
        basketRepository.basketLiveData = basketLiveData
        basketRepository.getBasket()
    }

    fun getTotalPrice(products: ArrayList<ViewType?>): String? {

        var totalPrice = 0

        for (product in products) {

            val basketModel = product as BasketModel
            var priceString = basketModel.price

            priceString?.let {
                if (it.contains('.')) {
                    priceString = it.substring(0, it.indexOf('.'))
                }
            }
            if (priceString == null || priceString == "null" || priceString?.isEmpty()!!) {
                priceString = "0"
            }
            var price = priceString?.toFloat()!!
            price *= basketModel.quantity?.toFloat()!!
            totalPrice = totalPrice.plus(price.toInt())
        }
        return totalPrice.toString()
    }

    fun basketIsEmpty() {

        basketStateLiveData = MutableLiveData()
        basketStateRepository.basketStateLiveData = basketStateLiveData
        basketStateRepository.checkBasketItemCount()
    }

    fun getProductQuantityFromBasket(productModel: ProductModel) {

        productQuantityFromBasketLiveData = MutableLiveData()
        productQuantityFromBasketRepository.productQuantityFromBasket = productQuantityFromBasketLiveData
        productQuantityFromBasketRepository.getProductQuantityFromBasket(productModel)

    }

    fun getProductQuantityFromBasket(basketModel: BasketModel) {

        productQuantityFromBasketLiveData = MutableLiveData()
        productQuantityFromBasketRepository.productQuantityFromBasket =
            productQuantityFromBasketLiveData
        productQuantityFromBasketRepository.getProductQuantityFromBasket(
            getProductModelFromBasketModel(basketModel)
        )

    }

    fun getProductModelFromBasketModel(basketModel: BasketModel): ProductModel {

        val basketModelJson = Gson().toJson(basketModel)
        return Gson().fromJson(basketModelJson, ProductModel::class.java)
    }

    fun createOrder(basketList: ArrayList<BasketModel>) {

        val lineItems: ArrayList<OrderModel.Order.LineItem> = ArrayList()
        for (basketItem in basketList) {
            val quantity = basketItem.quantity
            val title = basketItem.title
            val price = basketItem.price
            lineItems.add(OrderModel.Order.LineItem(price!!, quantity!!, title!!))
        }

        val orderModel = OrderModel(
            OrderModel.Order(
                OrderModel.Order.Customer(SharedHelper.getKey("userId")?.toLong()!!), "pending", lineItems
            )
        )

        viewModelScope.launch {
            createOrderLiveData = MutableLiveData()
            val createOrderResponse = createOrderRepository.createOrder(orderModel)
            createOrderResponse?.let {
                createOrderLiveData.value = it.isSuccessful && it.code() == 200
            } ?: run {
                createOrderLiveData.value = false
            }
        }
    }

    fun clearBasket() {

        clearBasketLiveData = MutableLiveData()
        clearBasketRepository.clearBasketLiveData = clearBasketLiveData
        clearBasketRepository.clearBasket()
    }
}
