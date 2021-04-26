package bazaar.tech.com.quantity

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import bazaar.tech.com.AppApplication
import bazaar.tech.com.constant.Checkout
import bazaar.tech.com.product.model.ProductModel
import ithd.bazaar.app.R
import kotlinx.android.synthetic.main.fragment_quantity_sheet.*


open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var onClick: ((action: String, quantity: Int, model: ProductModel, quantityDialog: RoundedBottomSheetDialogFragment) -> Unit?)? =
        null
    private var productModel: ProductModel? = null
    private var productQuantityFromBasket: Int? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupDialogHeight(bottomSheetDialog)
        }
        return dialog
    }

    fun addListener(onClick: ((action: String, quantity: Int, model: ProductModel, quantityDialog: RoundedBottomSheetDialogFragment) -> Unit?)?) {
        this.onClick = onClick
    }

    private fun setupDialogHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        val windowHeight = (quantityDialogButton.y).toInt() + ((quantityDialogButton.height) * 2)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.fragment_quantity_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        setUpUI()
        onClicked()
    }

    private fun onClicked() {
        // handle quantity plus and minus clicking
        plusLayout.setOnClickListener {

            changeState(true)
            quantityValue.text = quantityValue
                .text.toString().toInt().plus(1).toString()

            calculateTotalPrice(productModel?.price!!, quantityValue.text.toString())
        }


        minusLayout.setOnClickListener {

            if (quantityValue.text.toString().toInt() == 0) {

                return@setOnClickListener
            }
            quantityValue.text = quantityValue
                .text.toString().toInt().minus(1).toString()
            if (quantityValue.text.toString().toInt() == 0) {
                changeState(false)
            }

            calculateTotalPrice(productModel?.price!!, quantityValue.text.toString())
        }

        quantityDialogButton.setOnClickListener {

            quantityProgress.visibility = View.VISIBLE
            if (quantityValue.text.toString().toInt() == 0) {
                onClick?.let { it1 -> it1(Checkout.ACTION_REMOVE, 0, productModel!!, this) }
            } else {
                onClick?.let { it1 ->
                    it1(
                        Checkout.ACTION_UPDATE,
                        quantityValue.text.toString().toInt(),
                        productModel!!,
                        this
                    )
                }
            }
        }
    }

    private fun getArgumentData() {

        arguments?.let {
            productModel = it["productModel"] as ProductModel?
            productQuantityFromBasket = it["productQuantityFromBasket"] as Int?
        }
    }

    private fun setUpUI() {

        quantityThumbnailImage.setCornerRadiiDP(
            10f, 10f, 10f, 10f
        )
        val imageUrl = productModel?.imageUrl

        if (!TextUtils.isEmpty(imageUrl) && imageUrl != "null") {
            quantityThumbnailImage.load(imageUrl)
        } else {
            quantityThumbnailImage.setBackgroundResource(R.drawable.quantity_dialog_placeholder)
        }

        quantityValue.text = productQuantityFromBasket.toString()

        quantityTitle.text = productModel?.title

        enableMinus(true)

        calculateTotalPrice(productModel?.price!!, quantityValue.text.toString())
    }

    private fun calculateTotalPrice(singleProductPrice: String, quantity: String) {
        val quantityInt = quantity.toDouble()
        val singleProductPriceInt = singleProductPrice.toDouble()

        val totalPrice = (quantityInt * singleProductPriceInt).toInt()
        quantityTotalValue.text = getString(R.string.quantity_total_value, totalPrice.toString())
    }

    private fun enableMinus(enabled: Boolean) {
        if (!enabled) {
            // change minus color to inactive
            minusIcon.imageTintList =
                ContextCompat
                    .getColorStateList(AppApplication.appContext, R.color.quantity_dialog_minus)
            minusLayout.backgroundTintList =
                ContextCompat
                    .getColorStateList(AppApplication.appContext, R.color.quantity_dialog_minus)
        } else {
            minusIcon.imageTintList = null
            minusIcon.backgroundTintList = null
            minusLayout.backgroundTintList = null
        }
    }

    private fun changeState(activate: Boolean) {
        if (!activate) {
            quantityDialogButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.corner_majenta_layout)
            quantityDialogButtonText.text =
                context?.getString(R.string.quantity_dialog_button_remove_text)
            enableMinus(false)
        } else {
            quantityDialogButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.corner_black_layout)
            quantityDialogButtonText.text =
                context?.getString(R.string.quantity_dialog_button_text)
            enableMinus(true)
        }
    }

    fun hideProgress() {
        quantityProgress.visibility = View.GONE
    }
}