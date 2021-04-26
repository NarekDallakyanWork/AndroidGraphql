package bazaar.tech.com.profile.view.fragment

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import ithd.bazaar.app.R
import kotlinx.android.synthetic.main.log_out_dialog.view.*


class LogOutDialogFragment : DialogFragment() {

    companion object {
        private lateinit var onClicked: (onBack: Boolean, onLogout: Boolean) -> Unit
        fun newInstance(onClicked: (onBack: Boolean, onLogout: Boolean) -> Unit): LogOutDialogFragment? {
            this.onClicked = onClicked
            return LogOutDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.log_out_dialog, container, false)
    }

    @SuppressLint("WrongConstant")
    override fun onStart() {
        super.onStart()
        val display: Display? = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        val width: Int = size.x
        dialog?.window?.setLayout(width - 60, WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.log_out_dialog_bg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        handleClicked(view)
    }

    private fun handleClicked(view: View) {

        view.logOutGoBack.setOnClickListener {
            dismiss()
            onClicked(true, false)
        }

        view.logOutButton.setOnClickListener {
            onClicked(false, true)
        }
    }

}