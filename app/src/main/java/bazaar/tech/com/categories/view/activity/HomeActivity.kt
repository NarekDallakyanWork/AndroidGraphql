package bazaar.tech.com.categories.view.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bazaar.tech.com.base.activity.BaseActivity
import bazaar.tech.com.base.widget.BazaarTabBar
import bazaar.tech.com.basket.view.fragment.BasketFragment
import bazaar.tech.com.basket.viewmodel.BasketViewModel
import bazaar.tech.com.categories.view.fragment.CategoriesFragment
import bazaar.tech.com.product.view.fragment.ProductFragment
import bazaar.tech.com.profile.view.fragment.ProfileFragment
import ithd.bazaar.app.R
import kotlinx.android.synthetic.main.activity_categories.*

class HomeActivity : BaseActivity() {

    private var bazaarTabBar: BazaarTabBar? = null

    private lateinit var basketViewModel: BasketViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initViewModel()
        checkIsBasketIsEmpty()
        initUI()
        tabItemClicked()
        openFragment(R.id.fragmentContainer, CategoriesFragment(), null)
    }

    private fun checkIsBasketIsEmpty() {

        basketViewModel.basketIsEmpty()
        val observer = Observer<Boolean?> {
            if (it == null) return@Observer

            if (it) {
                updateTabBarBadge()
            } else {
                updateTabBarBadge()
            }
        }

        // observe basket state liveData
        basketViewModel
            .basketStateLiveData
            .observe(this, observer)
    }

    private fun initViewModel() {
        basketViewModel = ViewModelProviders.of(this).get(BasketViewModel::class.java)
    }

    private fun initUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }

    fun showProcessOrderLoader(show: Boolean) {
        if (show) {
            // hide toolbar and create full screen
            processOrderLoader.visibility = View.VISIBLE
        } else
            processOrderLoader.visibility = View.GONE
    }


    override fun getTabBar(): BazaarTabBar? {
        if (bazaarTabBar == null)
            bazaarTabBar = BazaarTabBar(bottom_tab_bar as ConstraintLayout)
        return bazaarTabBar
    }

    override fun hideTabBar() {
        bottom_tab_bar?.visibility = View.GONE
    }

    override fun showTabBar() {
        bottom_tab_bar?.visibility = View.VISIBLE
    }

    override fun tabItemClicked() {
        getTabBar()?.let { tabBar ->

            tabBar.addClickListener { tag ->
                when (tag) {
                    BazaarTabBar.HOME -> {
                        openFragment(
                            R.id.fragmentContainer,
                            CategoriesFragment()
                        )
                    }

                    BazaarTabBar.BASKET -> {
                        openFragment(
                            R.id.fragmentContainer,
                            BasketFragment()
                        )
                    }

                    BazaarTabBar.PROFILE -> {
                        openFragment(
                            R.id.fragmentContainer,
                            ProfileFragment()
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        fragment?.let {
            if (fragment is CategoriesFragment) {
                finish()
                return
            } else if (fragment is BasketFragment) {
                val basketFragment = fragment as BasketFragment
                if (basketFragment.quantityDialogIsShow()) {
                    basketFragment.hideQuantityDialog()
                    showTabBar()
                    return
                }
                println()
            } else if (fragment is ProductFragment) {
                val productFragment = fragment as ProductFragment
                if (productFragment.quantityDialogIsShow()){
                    productFragment.hideQuantityDialog()
                    showTabBar()
                    return
                }
            }
        }
        super.onBackPressed()
    }
}
