package bazaar.tech.com.basket.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import bazaar.tech.com.base.extention.showMessage
import bazaar.tech.com.base.extention.showQuantityDialog
import com.google.android.material.appbar.CollapsingToolbarLayout
import bazaar.tech.com.base.fragment.BaseFragment
import bazaar.tech.com.base.recyclerview.NormalRecyclerViewAdapter
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.base.widget.AlertMessage
import bazaar.tech.com.basket.model.BasketModel
import bazaar.tech.com.basket.viewmodel.BasketViewModel
import bazaar.tech.com.categories.view.fragment.CategoriesFragment
import bazaar.tech.com.constant.Checkout
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.product.model.ProductModel
import bazaar.tech.com.quantity.RoundedBottomSheetDialogFragment
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.FragmentBasketBinding
import kotlinx.android.synthetic.main.basket_content_scrolling.view.*
import kotlinx.android.synthetic.main.basket_empty_layout.view.*
import kotlinx.android.synthetic.main.basket_not_empty_layout.view.*
import kotlinx.android.synthetic.main.fragment_basket.*
import kotlinx.android.synthetic.main.process_to_order_layout.view.*


/**
 * A simple [Fragment] subclass.
 */
class BasketFragment : BaseFragment() {


    private lateinit var binding: FragmentBasketBinding
    private lateinit var quantityDialog: RoundedBottomSheetDialogFragment

    private lateinit var basketViewModel: BasketViewModel

    private lateinit var normalRecyclerAdapter: NormalRecyclerViewAdapter

    private var basketList = ArrayList<BasketModel>()

    private val menuFragment = RoundedBottomSheetDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketBinding.inflate(inflater)
        initViewModel()
        getBasket()
        onClicked()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCollapsing()
    }

    private fun initCollapsing() {

        val collapsingToolbarLayout: CollapsingToolbarLayout =
            binding.basketNotEmptyLayout.toolbar_layout
        collapsingToolbarLayout.isTitleEnabled = true
        collapsingToolbarLayout.title = getString(R.string.basket_title)
        collapsingToolbarLayout.apply {
            // change collapse and expand title states text [size] and [font]
            setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
            setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        }

        val basketEmptyTitle: TextView =
            binding.basketEmptyLayout.basketEmptyTitle
        basketEmptyTitle.text = getString(R.string.basket_title)

        binding.basketNotEmptyLayout.processOrder.storeName.text =
            SharedHelper.getKey("companyName")
                .plus(" " + getString(R.string.basket_order_abc_title))
                .replace("  ", " ")
    }

    private fun onClicked() {

        binding.basketNotEmptyLayout.processOrder.process_pay.setOnClickListener {

            if (!quantityDialogIsShow() && basketList.size > 0) {
                showProcessOrderLoader(true)
                createOrder(basketList)
            }
        }

        binding.basketEmptyLayout.basket_empty_button.setOnClickListener {
            openFragment(R.id.fragmentContainer, CategoriesFragment())
        }
    }

    private fun createOrder(basketList: ArrayList<BasketModel>) {

        basketViewModel.createOrder(basketList)
        val observer = Observer<Boolean?> {
            if (!it!!) {
                showProcessOrderLoader(false)
                binding.basketRoot.showMessage(
                    context,
                    getString(R.string.create_order_error_message),
                    AlertMessage.CENTER,
                    8
                )
            } else {
                clearBasket()
            }
        }

        // observe create order liveData
        basketViewModel
            .createOrderLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun clearBasket() {

        basketViewModel.clearBasket()
        val observer = Observer<Boolean?> {
            showProcessOrderLoader(false)
            updateTabBarBadge()
            openFragment(R.id.fragmentContainer, OrderReceivedFragment())
        }
        // observe clear basket liveData
        basketViewModel
            .clearBasketLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun getBasket() {

        binding.basketProgress.visibility = View.VISIBLE
        basketViewModel.getBasket()
        val observer = Observer<ArrayList<ViewType?>> {
            binding.basketProgress.visibility = View.GONE
            if (it != null) {
                @Suppress("UNCHECKED_CAST")
                basketList = it as ArrayList<BasketModel>
                handleBasketState(it)
            }
        }

        basketViewModel
            .basketLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun handleBasketState(products: ArrayList<ViewType?>) {

        if (basketList.isEmpty()) {
            showUI(true)
        } else {
            showUI(false)
            initBasketAdapter(products)
        }
    }

    private fun showUI(isEmpty: Boolean) {
        if (!isEmpty) {
            binding.basketNotEmptyLayout.visibility = View.VISIBLE
            binding.basketEmptyLayout.visibility = View.GONE

        } else {
            binding.basketNotEmptyLayout.visibility = View.GONE
            binding.basketEmptyLayout.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        basketViewModel =
            ViewModelProviders.of(this).get(BasketViewModel::class.java)
    }

    private fun initBasketAdapter(products: ArrayList<ViewType?>) {

        binding.basketNotEmptyLayout.basketIncludeLayout.basketRecyclerView.layoutManager =
            GridLayoutManager(context, 1)
        normalRecyclerAdapter = NormalRecyclerViewAdapter()
        normalRecyclerAdapter.submitList(products)
        normalRecyclerAdapter.addListener {
            getProductQuantityFromBasket(it as BasketModel)
        }
        binding.basketNotEmptyLayout.basketIncludeLayout.basketRecyclerView.adapter =
            normalRecyclerAdapter
        binding.basketNotEmptyLayout.processOrder.basketTotalPrice.text =
            getString(R.string.currency).plus(" "+basketViewModel.getTotalPrice(products))

        initUI()
    }

    private fun initUI() {

        val basketNestedLayoutParams: FrameLayout.LayoutParams =
            binding.basketNotEmptyLayout.basketIncludeLayout.basketSubNestedLayout.layoutParams as FrameLayout.LayoutParams
        val processOrderLayoutHeight = binding.basketNotEmptyLayout.processOrder.height
        basketNestedLayoutParams.setMargins(
            0,
            0,
            0,
            processOrderLayoutHeight
        )
    }

    private fun getProductQuantityFromBasket(model: BasketModel) {

        binding.basketProgress.visibility = View.VISIBLE
        basketViewModel.getProductQuantityFromBasket(model)
        val observer = Observer<Int> {
            binding.basketProgress.visibility = View.GONE
            showQuantityDialog(model, it)
        }

        // observe productQuantityFromBasket liveData
        basketViewModel
            .productQuantityFromBasketLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun showQuantityDialog(
        model: BasketModel,
        productQuantityFromBasket: Int
    ) {

        val bundle = Bundle()
        bundle.putParcelable("productModel", basketViewModel.getProductModelFromBasketModel(model))
        bundle.putInt("productQuantityFromBasket", productQuantityFromBasket)
        menuFragment.arguments = bundle
        menuFragment.show(parentFragmentManager, menuFragment.tag)
        menuFragment.addListener(onClick = { action, quantity, mModel, quantityDialog ->
            this.quantityDialog = quantityDialog
            handleQuantityOnClick(action, quantity, mModel)
        })
    }

    private fun handleQuantityOnClick(
        action: String,
        quantity: Int,
        model: ProductModel
    ) {
        when (action) {
            Checkout.ACTION_REMOVE -> {
                removeProductFromBasket(quantity, model)
            }

            Checkout.ACTION_UPDATE -> {
                addProductToBasket(quantity, model)
            }
        }
    }

    private fun removeProductFromBasket(quantity: Int, model: ProductModel) {

        binding.basketProgress.visibility = View.VISIBLE
        basketViewModel.removeFromBasket(quantity, model)
        val observer = Observer<Boolean> {
            quantityDialog.hideProgress()
            quantityDialog.dismiss()
            checkIsBasketIsEmpty()
            binding.basketProgress.visibility = View.GONE
            if (it) {
                binding.basketRoot.showMessage(
                    context,
                    getString(R.string.sub_product_removed_from_basket_message),
                    AlertMessage.CENTER
                )
            } else {
                binding.basketRoot.showMessage(
                    context,
                    getString(R.string.basket_error_message)
                )
            }
            getBasket()
        }
        basketViewModel
            .checkoutLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun addProductToBasket(quantity: Int, model: ProductModel) {

        binding.basketProgress.visibility = View.VISIBLE
        basketViewModel.addToBasket(quantity, model)
        val observer = Observer<Boolean> {
            quantityDialog.hideProgress()
            quantityDialog.dismiss()
            checkIsBasketIsEmpty()
            binding.basketProgress.visibility = View.GONE
            if (it) {
                binding.basketRoot.showMessage(
                    context,
                    getString(R.string.add_to_basket_message),
                    AlertMessage.CENTER
                )
            } else {
                binding.basketRoot.showMessage(
                    context,
                    getString(R.string.basket_error_message)
                )
            }
            getBasket()
        }
        basketViewModel
            .checkoutLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun checkIsBasketIsEmpty() {

        basketViewModel.basketIsEmpty()
        val observer = Observer<Boolean?> {
            updateTabBarBadge()
        }

        // observe basket state liveData
        basketViewModel
            .basketStateLiveData
            .observe(viewLifecycleOwner, observer)
    }

    fun quantityDialogIsShow(): Boolean {
        val quantityDialog: RelativeLayout? =
            basketRoot.findViewById(R.id.quantityDialogRoot)
        return quantityDialog != null
    }

    fun hideQuantityDialog() {
        binding.basketRoot.showQuantityDialog(show = false, activity = activity as AppCompatActivity)
    }
}
