package bazaar.tech.com.categories.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import bazaar.tech.com.auth.model.State
import bazaar.tech.com.base.extention.showMessage
import bazaar.tech.com.base.fragment.BaseFragment
import bazaar.tech.com.base.recyclerview.NormalRecyclerViewAdapter
import bazaar.tech.com.base.recyclerview.ViewType
import bazaar.tech.com.categories.model.CategoriesResponseModel
import bazaar.tech.com.categories.viewmodel.CategoriesViewModel
import bazaar.tech.com.product.view.fragment.ProductFragment
import ithd.bazaar.app.R
import ithd.bazaar.app.databinding.FragmentCategoriesBinding
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CategoriesFragment : BaseFragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var viewModel: CategoriesViewModel
    private lateinit var normalRecyclerAdapter: NormalRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(inflater)
        initViewModel()
        getCategories()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
    }

    private fun getCategories() {
        binding.categoriesProgress.visibility = View.VISIBLE
        viewModel.getCategories()
        val observer = Observer<CategoriesResponseModel> {
            binding.categoriesProgress.visibility = View.GONE
            when (it.state) {
                State.SUCCESS -> {
                    initCategoriesAdapter(it.categories!!)
                }
                State.FAILURE -> {
                    binding.categoryRoot.showMessage(
                        context,
                        getString(R.string.category_failure_message)
                    )
                }
                else -> {
                }
            }
        }

        // observe
        viewModel
            .categoriesLiveData
            .observe(viewLifecycleOwner, observer)
    }

    private fun initCategoriesAdapter(categories: ArrayList<ViewType?>) {
        binding.categoriesRecyclerView.layoutManager = GridLayoutManager(context, 1)
        normalRecyclerAdapter = NormalRecyclerViewAdapter()
        normalRecyclerAdapter.submitList(categories)
        normalRecyclerAdapter.addListener {
            goToProductFragment(it)
        }
        binding.categoriesRecyclerView.adapter = normalRecyclerAdapter
    }

    private fun goToProductFragment(viewType: ViewType) {
        openFragment(
            R.id.fragmentContainer,
            ProductFragment.newInstance(Bundle().apply {
                putString(
                    ProductFragment.CATEGORY_HANDLE,
                    (viewType as CategoriesResponseModel.CategoryModel).handle
                )
                putString(
                    ProductFragment.CATEGORY_TITLE,
                    viewType.title
                )
            })
        )
    }

}
