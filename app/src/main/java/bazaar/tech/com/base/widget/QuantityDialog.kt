package bazaar.tech.com.base.widget

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.api.load
import bazaar.tech.com.AppApplication
import bazaar.tech.com.categories.view.activity.HomeActivity
import bazaar.tech.com.constant.Checkout.ACTION_REMOVE
import bazaar.tech.com.constant.Checkout.ACTION_UPDATE
import bazaar.tech.com.product.model.ProductModel
import ithd.bazaar.app.R


class QuantityDialog(private val rootView: ViewGroup, private val context: Context) {

    private var onClick: ((action: String, quantity: Int, model: ProductModel, quantityDialog: QuantityDialog) -> Unit?)? =
        null

    companion object {
        // Singleton prevents multiple instances of QuantityDialog
        @Volatile
        private var INSTANCE: QuantityDialog? = null

        fun getInstance(rootView: ViewGroup, context: Context): QuantityDialog {
            if (INSTANCE != null) {
                return INSTANCE as QuantityDialog
            }
            synchronized(this) {
                return QuantityDialog(rootView, context)
            }
        }
    }

    private var model: ProductModel? = null
    private var quantityLayout: RelativeLayout? = null
    private lateinit var minusOval: RelativeLayout
    private lateinit var plusOval: RelativeLayout
    lateinit var quantityValue: TextView
    lateinit var quantityImage: ImageView
    lateinit var minusIcon: ImageView
    lateinit var quantityButton: RelativeLayout
    lateinit var quantityButtonText: TextView
    lateinit var quantityProgress: ProgressBar

    fun addListener(onClick: ((action: String, quantity: Int, model: ProductModel, quantityDialog: QuantityDialog) -> Unit?)?) {
        this.onClick = onClick
    }

    fun show(quantity: Int, model: ProductModel?) {
        this.model = model
        hideTabBar()

        enableDisableView(rootView, false)

        addQuantityDialogView(quantity, model?.imageUrl)
    }

    private fun hideTabBar() {

        // hide toolbar and create full screen
        val homeActivity: HomeActivity? = context as HomeActivity
        homeActivity?.hideTabBar()
        homeActivity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun showTabBar() {

        // hide toolbar and create full screen
        val homeActivity: HomeActivity? = context as HomeActivity
        homeActivity?.showTabBar()
        homeActivity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun enableDisableView(parentView: ViewGroup?, enabled: Boolean) {

        if (parentView == null) return
        parentView.isEnabled = enabled
        for (i in 0 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            child.isEnabled = enabled
        }
    }

    private fun removeQuantityDialogView(): Boolean {
        if (quantityLayout == null)
            quantityLayout = rootView.findViewById(R.id.quantityDialogRoot)
        if (quantityLayout != null) {
            rootView.removeView(quantityLayout)
            rootView.requestLayout()
            return true
        }
        return false
    }

    private fun addQuantityDialogView(quantity: Int, imageUrl: String?): RelativeLayout {
        quantityLayout =
            LayoutInflater.from(context).inflate(
                R.layout.quantity_dialog,
                rootView,
                false
            ) as RelativeLayout

        // find views
        initViews(quantityLayout!!)
        initUI(quantity, imageUrl)
        if (quantityValue.text.toString().toInt() == 0) {
            changeState(false)
        }

        // handle quantity plus and minus clicking
        plusOval.setOnClickListener {

            changeState(true)
            quantityValue.text = quantityValue
                .text.toString().toInt().plus(1).toString()
        }

        minusOval.setOnClickListener {

            if (quantityValue.text.toString().toInt() == 0) {
                return@setOnClickListener
            }
            quantityValue.text = quantityValue
                .text.toString().toInt().minus(1).toString()
            if (quantityValue.text.toString().toInt() == 0) {
                changeState(false)
            }
        }

        quantityButton.setOnClickListener {

            quantityProgress.visibility = View.VISIBLE
            if (quantityValue.text.toString().toInt() == 0) {
                onClick?.let { it1 -> it1(ACTION_REMOVE, 0, model!!, this) }
            } else {
                onClick?.let { it1 ->
                    it1(
                        ACTION_UPDATE,
                        quantityValue.text.toString().toInt(),
                        model!!,
                        this
                    )
                }
            }
        }

        // change layout gravity add dialog view
        val quantityLayoutParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        quantityLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT)
        quantityLayout?.layoutParams = quantityLayoutParam

        rootView.addView(quantityLayout)
        return quantityLayout!!
    }

    private fun initUI(quantity: Int, imageUrl: String?) {
        enableMinus(true)
        // add corresponding values
        quantityValue.text = quantity.toString()
        if (!TextUtils.isEmpty(imageUrl) && imageUrl != "null") {
            quantityImage.load(imageUrl)
        } else {
            quantityImage.setBackgroundResource(R.drawable.quantity_dialog_placeholder)
        }
    }

    private fun enableMinus(enabled: Boolean) {
        if (!enabled) {
            // change minus color to inactive
            minusIcon.imageTintList =
                ContextCompat
                    .getColorStateList(AppApplication.appContext, R.color.quantity_dialog_minus)
            minusOval.backgroundTintList =
                ContextCompat
                    .getColorStateList(AppApplication.appContext, R.color.quantity_dialog_minus)
        } else {
            minusIcon.imageTintList = null
            minusIcon.backgroundTintList = null
            minusOval.backgroundTintList = null
        }
    }

    private fun initViews(quantityLayout: RelativeLayout) {
        minusOval = quantityLayout.findViewById(R.id.minusLayout)
        plusOval = quantityLayout.findViewById(R.id.plusLayout)
        quantityValue = quantityLayout.findViewById(R.id.quantityValue)
        quantityImage = quantityLayout.findViewById(R.id.quantityImage)
        minusIcon = quantityLayout.findViewById(R.id.minusIcon)
        quantityButton = quantityLayout.findViewById(R.id.quantityDialogButton)
        quantityButtonText =
            quantityLayout.findViewById(R.id.quantityDialogButtonText)
        quantityProgress = quantityLayout.findViewById(R.id.quantityProgress)
    }

    private fun changeState(activate: Boolean) {
        if (!activate) {
            quantityButton.background =
                ContextCompat.getDrawable(context, R.drawable.corner_majenta_layout)
            quantityButtonText.text =
                context.getString(R.string.quantity_dialog_button_remove_text)
            enableMinus(false)
        } else {
            quantityButton.background =
                ContextCompat.getDrawable(context, R.drawable.corner_black_layout)
            quantityButtonText.text =
                context.getString(R.string.quantity_dialog_button_text)
            enableMinus(true)
        }
    }

    fun dismiss() {
        quantityProgress.visibility = View.GONE
        removeQuantityDialogView()
        showTabBar()
    }

    fun hideProgress() {
        quantityProgress.visibility = View.GONE
    }

    fun hideDialog(): Boolean {
        return removeQuantityDialogView()
    }
}