package bazaar.tech.com.base.bindingadapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import bazaar.tech.com.auth.viewmodel.AuthViewModel
import bazaar.tech.com.base.extention.isEmail
import bazaar.tech.com.base.extention.isPassword
import ithd.bazaar.app.R

@BindingAdapter("passwordValidator", "viewModel", "errorView")
fun passwordValidator(
    editText: EditText,
    password: String?,
    viewModel: AuthViewModel,
    errorView: TextView
) {

    val context: Context? = editText.context
    context?.let {

        if (TextUtils.isEmpty(password)) {

            editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray)
            errorView.visibility = View.GONE
            viewModel.passwordIsValid = false
            viewModel.fieldValidationLiveData.value = false
        } else {

            if (!password!!.isPassword()) {
                editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                errorView.text = context.getString(R.string.login_password_error)
                errorView.visibility = View.VISIBLE
                viewModel.passwordIsValid = false
                viewModel.fieldValidationLiveData.value = false
            } else {
                editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray)
                errorView.visibility = View.GONE
                viewModel.passwordIsValid = true
                viewModel.fieldValidationLiveData.value = true

            }
        }
    }
}

@BindingAdapter("phoneValidator", "viewModel", "errorView")
fun phoneValidator(
    editText: EditText,
    phone: String?,
    viewModel: AuthViewModel,
    errorView: TextView
) {

    val context: Context? = editText.context
    context?.let {

        if (TextUtils.isEmpty(phone)) {

            editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray)
            errorView.visibility = View.GONE
            viewModel.phoneIsValid = false
            viewModel.fieldValidationLiveData.value = false
        } else {

            if (!phone!!.isEmail()) {
                editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                errorView.text = context.getString(R.string.login_phone_error)
                errorView.visibility = View.VISIBLE
                viewModel.phoneIsValid = false
                viewModel.fieldValidationLiveData.value = false
            } else {
                editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray)
                errorView.visibility = View.GONE
                viewModel.phoneIsValid = true
                viewModel.fieldValidationLiveData.value = true
            }
        }
    }
}