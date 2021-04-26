package bazaar.tech.com.basket.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bazaar.tech.com.base.extention.resizeViewDependHeight
import bazaar.tech.com.base.fragment.BaseFragment
import bazaar.tech.com.categories.view.fragment.CategoriesFragment
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.FragmentOrderReceivedBinding

/**
 * A simple [Fragment] subclass.
 */
class OrderReceivedFragment : BaseFragment() {

    private lateinit var binding: FragmentOrderReceivedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderReceivedBinding.inflate(inflater)
        configureUI()
        onClicked()
        return binding.root
    }

    private fun onClicked() {
        binding.backToCategoryBtn.setOnClickListener {
            openFragment(R.id.fragmentContainer, CategoriesFragment())
        }
    }

    private fun configureUI() {
        binding.orderReceivedImage.resizeViewDependHeight(requireActivity(), 40f)
    }

}
