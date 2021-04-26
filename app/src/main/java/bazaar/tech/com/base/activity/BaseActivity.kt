package bazaar.tech.com.base.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bazaar.tech.com.base.widget.BazaarTabBar

abstract class BaseActivity : AppCompatActivity() {

    fun openFragment(
        resId: Int,
        fragment: Fragment,
        bundle: Bundle? = null
    ) {

        val existedFragment: Fragment? =
            supportFragmentManager.findFragmentByTag(fragment.javaClass.name)

        if (existedFragment != null)
            supportFragmentManager
                .beginTransaction()
                .remove(existedFragment)
                .commit()

        if (bundle != null)
            fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(resId, fragment, fragment.javaClass.name)
            .addToBackStack(null)
            .commit()
    }

    fun clearBackStack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun goTo(activity: Class<out AppCompatActivity>, popStack: Boolean) {

        val intent = Intent(this, activity)
        if (popStack)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        if (popStack)
            finish()
    }

    fun updateTabBarBadge() {
        getTabBar()?.handleBasketState()
    }

    abstract fun getTabBar(): BazaarTabBar?

    abstract fun hideTabBar()

    abstract fun showTabBar()

    abstract fun tabItemClicked()
}
