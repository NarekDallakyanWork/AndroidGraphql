package bazaar.tech.com.auth.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bazaar.tech.com.auth.model.LoginResponseModel
import bazaar.tech.com.auth.model.State
import bazaar.tech.com.auth.viewmodel.AuthViewModel
import bazaar.tech.com.base.activity.BaseActivity
import bazaar.tech.com.base.extention.resizeViewDependHeight
import bazaar.tech.com.base.extention.showMessage
import bazaar.tech.com.base.widget.BazaarTabBar
import bazaar.tech.com.categories.view.activity.HomeActivity
import bazaar.tech.com.helper.SharedHelper
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mBinding: ActivityLoginBinding
    override fun getTabBar(): BazaarTabBar? {
        return null
    }

    override fun hideTabBar() {}
    override fun showTabBar() {}

    override fun tabItemClicked() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLoggedIn()) return
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initUI()
        initViewModel()
        initBinding()
        configureUI()
        observeFieldValidation()
        observeLoginLiveData()
        onClicked()
    }

    private fun observeFieldValidation() {
        val observer = Observer<Boolean> {
            mBinding.LOGINBUTTON.isClickable = mViewModel.phoneIsValid && mViewModel.passwordIsValid
        }

        mViewModel
            .fieldValidationLiveData
            .observe(this, observer)
    }

    private fun isLoggedIn(): Boolean {
        showStatusBar()
        val accessToken = SharedHelper.getKey("accessToken")
        val userId = SharedHelper.getKey("userId")

        if (accessToken == null
            || accessToken.isEmpty()
            || accessToken == "null"
            || userId == null
            || userId.isEmpty()
            || userId == "null"
        ) {
            SharedHelper.clearSharedPreferences()
            return false
        }

        goTo(HomeActivity::class.java, true)
        return true
    }

    private fun onClicked() {

        mBinding.LOGINBUTTON.setOnClickListener {
            if (mBinding.textView2.visibility != View.VISIBLE &&
                mBinding.textView3.visibility != View.VISIBLE
            ) {
                mViewModel.login()
            }
        }
    }

    private fun observeLoginLiveData() {

        val observer = Observer<LoginResponseModel> {

            when (it?.state) {
                State.IN_PROGRESS -> {

                    // show progress bar
                    mBinding.LOGINPROGRESSBAR.visibility = View.VISIBLE
                    phoneNumber.isCursorVisible = false
                    password.isCursorVisible = false
                }

                State.FAILURE -> {

                    // hide progress bar
                    mBinding.LOGINPROGRESSBAR.visibility = View.GONE
                    mBinding.LOGINROOT.showMessage(this, it.message)
                }

                State.SUCCESS -> {

                    // hide progress bar
                    mBinding.LOGINPROGRESSBAR.visibility = View.GONE
                    goTo(HomeActivity::class.java, true)
                }
            }
        }

        // observe
        mViewModel
            .loginLiveData
            .observe(this, observer)
    }

    private fun initUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }

    private fun showStatusBar() {

        // show toolbar and create full screen
        window?.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initBinding() {
        mBinding.mViewModel = mViewModel
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    private fun configureUI() {
        mBinding.LOGINCOVERIMAGE.resizeViewDependHeight(this, 45f)
    }
}
