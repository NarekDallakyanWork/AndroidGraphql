package bazaar.tech.com.product.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import bazaar.tech.com.base.extention.showMessage
import bazaar.tech.com.base.extention.showQuantityDialog
import com.google.android.material.appbar.CollapsingToolbarLayout
import bazaar.tech.com.base.fragment.BaseFragment
import bazaar.tech.com.base.recyclerview.PagingRecyclerView
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.base.widget.AlertMessage
import bazaar.tech.com.basket.view.fragment.BasketFragment
import bazaar.tech.com.basket.viewmodel.BasketViewModel
import bazaar.tech.com.constant.Checkout.ACTION_REMOVE
import bazaar.tech.com.constant.Checkout.ACTION_UPDATE
import bazaar.tech.com.product.model.ProductModel
import bazaar.tech.com.product.viewmodel.ProductByCategoryViewModel
import bazaar.tech.com.quantity.RoundedBottomSheetDialogFragment
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.FragmentProductBinding
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.product_content_scrolling.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProductFragment : BaseFragment() {

    companion object {
        const val CATEGORY_HANDLE = "category_handle"
        const val CATEGORY_TITLE = "category_title"

        fun newInstance(
            bundle: Bundle? = null
        ): ProductFragment {
            val fragment =
                ProductFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentProductBinding
    private lateinit var productByCategoryViewModel: ProductByCategoryViewModel
    private lateinit var basketViewModel: BasketViewModel
    private lateinit var adapter: PagingRecyclerView
    private lateinit var quantityDialog: RoundedBottomSheetDialogFragment
    private val menuFragment = RoundedBottomSheetDialogFragment()
    private var categoryHandle: String? = null
    private var categoryTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater)
        getArgumentData()
        onClicked()
        initViewModel()
        getProductByCategories()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCollapsing()
    }

    private fun initCollapsing() {
        val collapsingToolbarLayout: CollapsingToolbarLayout =
            binding.productToolbarLayout
        collapsingToolbarLayout.isTitleEnabled = true
        collapsingToolbarLayout.title = categoryTitle
        collapsingToolbarLayout.apply {
            // change collapse and expand title states text [size] and [font]
            setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        }
    }

    private fun getArgumentData() {
        arguments?.apply {
            categoryHandle = getString(CATEGORY_HANDLE)
            categoryTitle = getString(CATEGORY_TITLE)
        }
    }

    private fun onClicked() {

        binding.subProductByBack.setOnClickListener {
            if (!quantityDialogIsShow())
                activity?.onBackPressed()
        }
    }

    private fun initViewModel() {
        productByCategoryViewModel =
            ViewModelProviders.of(this).get(ProductByCategoryViewModel::class.java)
        basketViewModel =
            ViewModelProviders.of(this).get(BasketViewModel::class.java)
    }

    private fun getProductByCategories() {

        if (categoryHandle == null) return

        binding.subProductByProgressBar.visibility = View.VISIBLE
        productByCategoryViewModel.getProduct(categoryHandle!!)
        productByCategoryViewModel.observePagedLiveData().observe(viewLifecycleOwner, Observer {
            initSubProductAdapter()
            adapter.submitList(it)
        })

        productByCategoryViewModel.observeProductByCategoryStateLiveData()
            ?.observe(viewLifecycleOwner, Observer {
                when (it) {
                    ViewType.Type.ERROR -> {

                        binding.subProductRootLayout.showMessage(
                            context,
                            getString(R.string.sub_product_failure_message)
                        )
                    }
                    ViewType.Type.ON_ITEM_FRONT_LOADED -> {
                        binding.subProductByProgressBar.visibility = View.GONE
                    }
                    ViewType.Type.EMPTY -> {
                        binding.subProductByProgressBar.visibility = View.GONE
                        binding.subProductRootLayout.showMessage(
                            context,
                            getString(R.string.sub_product_empty_message)
                        )
                    }
                    else -> {
                    }
                }
            })
    }

    private fun initSubProductAdapter() {
        adapter = PagingRecyclerView()
        productIncludeLayout.sub_product_recyclerview.layoutManager = GridLayoutManager(context, 1)
        adapter.addListener {
            if (!quantityDialogIsShow())
                getProductQuantityFromBasket(it as ProductModel)
        }
        productIncludeLayout.sub_product_recyclerview.adapter = adapter
    }

    private fun getProductQuantityFromBasket(model: ProductModel) {

        binding.subProductByProgressBar.visibility = View.VISIBLE
        basketViewModel.getProductQuantityFromBasket(model)
        val observer = Observer<Int> {
            subProductByProgressBar.visibility = View.GONE
            showQuantityDialog(model, it)
        }

        // observe productQuantityFromBasket liveData
        basketViewModel
            .productQuantityFromBasketLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun showQuantityDialog(
        model: ProductModel,
        productQuantityFromBasket: Int
    ) {

        val bundle = Bundle()
        bundle.putParcelable("productModel", model)
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
            ACTION_REMOVE -> {
                removeProductFromBasket(quantity, model)
            }

            ACTION_UPDATE -> {
                addProductToBasket(quantity, model)
            }
        }
    }

    private fun removeProductFromBasket(quantity: Int, model: ProductModel) {

        binding.subProductByProgressBar.visibility = View.VISIBLE
        basketViewModel.removeFromBasket(quantity, model)
        val observer = Observer<Boolean> {
            quantityDialog.hideProgress()
            quantityDialog.dismiss()
            checkIsBasketIsEmpty()
            binding.subProductByProgressBar.visibility = View.GONE
            if (it) {
                binding.subProductRootLayout.showMessage(
                    context,
                    getString(R.string.sub_product_removed_from_basket_message),
                    AlertMessage.BOTTOM
                )
            } else {
                binding.subProductRootLayout.showMessage(
                    context,
                    getString(R.string.basket_error_message)
                )
            }
        }
        basketViewModel
            .checkoutLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun addProductToBasket(quantity: Int, model: ProductModel) {

        binding.subProductByProgressBar.visibility = View.VISIBLE
        basketViewModel.addToBasket(quantity, model)
        val observer = Observer<Boolean> {
            quantityDialog.hideProgress()
            quantityDialog.dismiss()
            checkIsBasketIsEmpty()
            binding.subProductByProgressBar.visibility = View.GONE
            if (it) {
                binding.subProductRootLayout.showMessage(
                    context,
                    getString(R.string.sub_product_add_to_basket_message),
                    messageIdentifier = "added",
                    gravity = AlertMessage.BOTTOM,
                    onClick = { messageIdentifier, alertMessageInstance ->
                        messageIdentifier?.let {
                            if (it == "added") {
                                openFragment(R.id.fragmentContainer, BasketFragment())
                            }
                        }
                    }
                )
                binding.subProductRootLayout
            } else {
                binding.subProductRootLayout.showMessage(
                    context,
                    getString(R.string.basket_error_message)
                )
            }
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
            subProductRootLayout.findViewById(R.id.quantityDialogRoot)
        return quantityDialog != null
    }

    fun hideQuantityDialog() {
        binding.subProductRootLayout.showQuantityDialog(
            show = false,
            activity = activity as AppCompatActivity
        )
    }
}
