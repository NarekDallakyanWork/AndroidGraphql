package bazaar.tech.com.base.widget

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bazaar.tech.com.AppApplication
import bazaar.tech.com.basket.view.fragment.BasketFragment
import bazaar.tech.com.categories.view.fragment.CategoriesFragment
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.profile.view.fragment.ProfileFragment
import ithd.bazaar.app.R


class BazaarTabBar(private var tabBarRoot: ConstraintLayout) {

    companion object{
        const val HOME = "home"
        const val BASKET = "basket"
        const val PROFILE = "profile"
    }
    private var onTabClicked: ((String) -> Unit?)? = null

    fun addClickListener(onTabClicked: (tag: String) -> Unit?) {
        this.onTabClicked = onTabClicked
        handleItemClicked()
    }

    fun autoChangeTabDependFragment(fragment: Fragment) {
        when (fragment) {
            is CategoriesFragment -> {
                updateUI(HOME)
            }
            is BasketFragment -> {
                updateUI(BASKET)
            }
            is ProfileFragment -> {
                updateUI(PROFILE)
            }
        }
    }

    private fun getItemByPosition(tag: String): RelativeLayout? {
        when (tag) {
            HOME -> {
                return tabBarRoot.findViewById(R.id.homeLayout)
            }
            BASKET -> {
                return tabBarRoot.findViewById(R.id.basketLayout)
            }
            PROFILE -> {
                return tabBarRoot.findViewById(R.id.profileLayout)
            }
        }
        return null
    }

    private fun handleItemClicked() {
        getItemByPosition(HOME)?.let {
            it.setOnClickListener {
                onTabClicked?.let {
                    it(HOME)
                }
            }
        }

        getItemByPosition(BASKET)?.let {
            it.setOnClickListener {
                onTabClicked?.let {
                    it(BASKET)
                }
            }
        }

        getItemByPosition(PROFILE)?.let {
            it.setOnClickListener {
                onTabClicked?.let {
                    it(PROFILE)
                }
            }
        }
    }

    private fun updateUI(tag: String) {
        when (tag) {
            HOME -> {
                releaseTabs()
                activateTab(HOME)
            }

            BASKET -> {
                releaseTabs()
                activateTab(BASKET)
            }

            PROFILE -> {
                releaseTabs()
                activateTab(PROFILE)
            }
        }
    }

    private fun releaseTabs() {

        // get tab root layout
        val homeLayout = getItemByPosition(HOME)
        val basketLayout = getItemByPosition(BASKET)
        val profileLayout = getItemByPosition(PROFILE)
        val basketBadgeTabLayout: RelativeLayout? =
            (basketLayout?.getChildAt(2) as ViewGroup)
                .getChildAt(0) as RelativeLayout?

        // get tab top lines
        val homeTopLine = homeLayout?.getChildAt(0)
        val basketTopLine = basketLayout.getChildAt(0)
        val profileTopLine = profileLayout?.getChildAt(0)

        // get tab icons
        val homeTabIcon: ImageView? = homeLayout?.getChildAt(1) as ImageView?
        val basketTabIcon: ImageView? = basketLayout.getChildAt(1) as ImageView?
        val profileTabIcon: ImageView? = profileLayout?.getChildAt(1) as ImageView?

        val homeTabText: TextView? = homeLayout?.findViewById(R.id.homeItemText)
        val basketTabText: TextView? = basketLayout.findViewById(R.id.basketItemText)
        val profileTabText: TextView? = profileLayout?.findViewById(R.id.profileItemText)

        // hide top lines
        homeTopLine?.visibility = View.GONE
        basketTopLine?.visibility = View.GONE
        profileTopLine?.visibility = View.GONE
        // hide basket badge
        basketBadgeTabLayout?.visibility = View.GONE

        // change icon color to inactive
        homeTabIcon?.imageTintList =
            ContextCompat
                .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_inactive)
        basketTabIcon?.imageTintList =
            ContextCompat
                .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_inactive)
        profileTabIcon?.imageTintList =
            ContextCompat
                .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_inactive)

        homeTabText?.setTextColor(
            ContextCompat.getColor(
                AppApplication.appContext,
                R.color.tab_bar_icon_inactive
            )
        )
        basketTabText?.setTextColor(
            ContextCompat.getColor(
                AppApplication.appContext,
                R.color.tab_bar_icon_inactive
            )
        )
        profileTabText?.setTextColor(
            ContextCompat.getColor(
                AppApplication.appContext,
                R.color.tab_bar_icon_inactive
            )
        )
    }

    fun handleBasketState() {
        val basketAmount = SharedHelper.getKey("basketAmount")
        val basketIsEmpty = TextUtils.isEmpty(basketAmount)
        val basketLayout = getItemByPosition(BASKET)
        val basketBadgeTabLayout: RelativeLayout? =
            (basketLayout?.getChildAt(2) as ViewGroup)
                .getChildAt(0) as RelativeLayout?
        val basketBadgeAmountTextView: TextView? = basketBadgeTabLayout?.getChildAt(1) as TextView?
        if (basketIsEmpty) {
            basketBadgeTabLayout?.visibility = View.GONE
        } else {
            basketBadgeTabLayout?.visibility = View.VISIBLE
            basketBadgeAmountTextView?.text = basketAmount
        }
    }

    private fun activateTab(tag: String) {

        when (tag) {
            HOME -> {
                val homeLayout = getItemByPosition(HOME)
                val homeTabText: TextView? = homeLayout?.findViewById(R.id.homeItemText)
                val homeTopLine = homeLayout?.getChildAt(0)
                homeTopLine?.visibility = View.VISIBLE
                val homeTabIcon: ImageView? = homeLayout?.getChildAt(1) as ImageView?
                homeTabIcon?.imageTintList =
                    ContextCompat
                        .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_active)
                homeTabText?.setTextColor(
                    ContextCompat.getColor(
                        AppApplication.appContext,
                        R.color.tab_bar_icon_active
                    )
                )
            }
            BASKET -> {
                val basketLayout = getItemByPosition(BASKET)
                val basketTabText: TextView? = basketLayout?.findViewById(R.id.basketItemText)
                val basketTopLine = basketLayout?.getChildAt(0)
                val basketTabIcon: ImageView? = basketLayout?.getChildAt(1) as ImageView?
                val basketBadgeTabLayout: RelativeLayout? =
                    (basketLayout?.getChildAt(2) as ViewGroup)
                        .getChildAt(0) as RelativeLayout?
                basketTopLine?.visibility = View.VISIBLE
                basketBadgeTabLayout?.visibility = View.VISIBLE
                basketTabIcon?.imageTintList =
                    ContextCompat
                        .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_active)
                basketTabText?.setTextColor(
                    ContextCompat.getColor(
                        AppApplication.appContext,
                        R.color.tab_bar_icon_active
                    )
                )
            }
            PROFILE -> {
                val profileLayout = getItemByPosition(PROFILE)
                val profileTabText: TextView? = profileLayout?.findViewById(R.id.profileItemText)
                val profileTopLine = profileLayout?.getChildAt(0)
                val profileTabIcon: ImageView? = profileLayout?.getChildAt(1) as ImageView?
                profileTopLine?.visibility = View.VISIBLE
                profileTabIcon?.imageTintList =
                    ContextCompat
                        .getColorStateList(AppApplication.appContext, R.color.tab_bar_icon_active)
                profileTabText?.setTextColor(
                    ContextCompat.getColor(
                        AppApplication.appContext,
                        R.color.tab_bar_icon_active
                    )
                )
            }
        }
        handleBasketState()
    }
}