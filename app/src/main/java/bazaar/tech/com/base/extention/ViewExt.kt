package bazaar.tech.com.base.extention

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import bazaar.tech.com.base.widget.AlertMessage
import bazaar.tech.com.base.widget.QuantityDialog
import bazaar.tech.com.product.model.ProductModel
import kotlin.math.roundToInt


fun View.resizeViewDependHeight(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val finalHeightValue = (height.toDouble() * ((percent / 100f))).roundToInt()
    val layoutParams: ViewGroup.LayoutParams = layoutParams
    layoutParams.height = finalHeightValue
    this.layoutParams = layoutParams
    requestLayout()
}

fun View.resizeViewDependHeight(activity: Activity, percent: Float, aaa: Int) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val finalHeightValue = (height.toDouble() * ((percent / 100f))).roundToInt()
    val layoutParams: RelativeLayout.LayoutParams = layoutParams as RelativeLayout.LayoutParams
    layoutParams.height = finalHeightValue
    layoutParams.setMargins(
        width / 6,
        top,
        width / 6,
        bottom
    )
    this.layoutParams = layoutParams
    requestLayout()
}

fun View.resizeViewDependWidth(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val finalHeightValue = (width.toDouble() * ((percent / 100f))).roundToInt()
    val layoutParams: ViewGroup.LayoutParams = layoutParams
    layoutParams.width = finalHeightValue
    this.layoutParams = layoutParams
    requestLayout()
}

fun View.marginTopView(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val finalValue = (width.toDouble() * ((percent / 100f))).roundToInt()
    val params = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(left, finalValue, right, bottom)
    this.layoutParams = params
    requestLayout()
}

fun View.marginLeftView(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val finalValue = (width.toDouble() * ((percent / 100f))).roundToInt()
    val params = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(finalValue, top, right, bottom)
    this.layoutParams = params
    requestLayout()
}

fun View.marginRightView(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val finalValue = (width.toDouble() * ((percent / 100f))).roundToInt()
    val params = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(left, top, finalValue, bottom)
    this.layoutParams = params
    requestLayout()
}

fun View.marginBottomView(activity: Activity, percent: Float) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val finalValue = (width.toDouble() * ((percent / 100f))).roundToInt()
    val params = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(left, top, right, finalValue)
    this.layoutParams = params
    requestLayout()
}

fun View.showMessage(
    context: Context?,
    message: String,
    gravity: String = AlertMessage.CENTER,
    cornerRadius: Int = 10,
    messageIdentifier: String? = null,
    onClick: ((messageIdentifier: String?, alertMessageInstance: AlertMessage) -> Unit?)? = null
) {
    context?.let {
        val alertMessage = AlertMessage(this as ViewGroup, it)
        alertMessage.messageIdentifier = messageIdentifier
        alertMessage.showMessage(message, gravity, cornerRadius, onClick)
    }
}

fun View.showQuantityDialog(
    activity: AppCompatActivity? = null,
    quantity: Int = 0,
    model: ProductModel? = null,
    onClick: ((action: String, quantity: Int, model: ProductModel, quantityDialog: QuantityDialog) -> Unit?)? = null,
    show: Boolean = true
): Boolean {
    activity?.let {
        if (show) {
            val quantityDialog = QuantityDialog.getInstance(this as ViewGroup, it)
            quantityDialog.addListener(onClick)
            quantityDialog.show(quantity, model)
            return true
        } else {
            val quantityDialog = QuantityDialog.getInstance(this as ViewGroup, it)
            val hideDialog = quantityDialog.hideDialog()
        }
    }
    return false
}