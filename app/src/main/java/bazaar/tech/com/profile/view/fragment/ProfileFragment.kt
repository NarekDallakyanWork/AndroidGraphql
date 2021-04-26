package bazaar.tech.com.profile.view.fragment

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bazaar.tech.com.auth.view.LoginActivity
import bazaar.tech.com.base.extention.showMessage
import bazaar.tech.com.base.fragment.BaseFragment
import bazaar.tech.com.base.widget.AlertMessage
import bazaar.tech.com.helper.SharedHelper
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_profile.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addProfileContent()
        onClicked()
    }

    private fun onClicked() {
        logOut.setOnClickListener {
            showDialog(onClicked = { back: Boolean, logOut: Boolean ->
                if (logOut) {
                    SharedHelper.clearSharedPreferences()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    activity?.finish()
                } else {

                }
            })
        }

        callUs.setOnClickListener {
            openWhatsAppConversation("923001229227", "Hello")
        }
    }

    private fun showDialog(onClicked: (onBack: Boolean, onLogout: Boolean) -> Unit) {
        // Create the fragment and show it as a dialog.
        val newFragment = LogOutDialogFragment.newInstance(onClicked)
        newFragment?.show(parentFragmentManager, LogOutDialogFragment::class.java.name)
    }

    private fun openWhatsAppConversation(
        number: String,
        message: String?
    ) {
        try {
            @Suppress("NAME_SHADOWING") var number = number
            number = number.replace(" ", "").replace("+", "")
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.setClassName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net")
            context?.startActivity(sendIntent)
        }catch (e: Exception) {
            profileRootLayout.showMessage(
                context,
                getString(R.string.profile_whats_app_not_installed),
                AlertMessage.CENTER
            )
        }
    }

    private fun addProfileContent() {

        val firstName = SharedHelper.getKey("firstName")
        val lastName = SharedHelper.getKey("lastName")
        val phone = SharedHelper.getKey("phone")
        val mCompanyName = SharedHelper.getKey("companyName")
        fullName.text = firstName.plus(" ").plus(lastName)
        companyName.text = mCompanyName
        phoneNumber.text = phone
    }
}
