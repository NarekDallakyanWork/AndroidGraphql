package bazaar.tech.com.base.fragment


import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import bazaar.tech.com.base.activity.BaseActivity
import bazaar.tech.com.base.widget.BazaarTabBar
import bazaar.tech.com.categories.view.activity.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    fun openFragment(resId: Int, fragment: Fragment, bundle: Bundle? = null) {

        if (activity == null) return

        (activity as BaseActivity).openFragment(resId, fragment, bundle)
    }

    private fun getTabBar(): BazaarTabBar? {
        return (activity as HomeActivity).getTabBar()
    }

    fun updateTabBarBadge() {
        (activity as HomeActivity).updateTabBarBadge()
    }

    fun showTabBar() {
        (activity as HomeActivity).showTabBar()
    }

    fun showProcessOrderLoader(show: Boolean) {
        if (show) {
            // hide toolbar and create full screen
            val homeActivity: HomeActivity? = context as HomeActivity
            homeActivity?.hideTabBar()
            homeActivity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val homeActivity: HomeActivity? = context as HomeActivity
            homeActivity?.showTabBar()
            homeActivity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        (activity as HomeActivity).showProcessOrderLoader(show)
    }

    override fun onResume() {
        super.onResume()
        showProcessOrderLoader(false)
        showTabBar()
        getTabBar()?.autoChangeTabDependFragment(this)
    }
}
