package bazaar.tech.com.base.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

open class PagingRecyclerView :
    PagedListAdapter<ViewType, MainViewHolder>(DIFF_CALLBACK) {

    private var onClick: ((ViewType) -> Unit?)? = null

    fun addListener(onClick: (data: ViewType) -> Unit?) {
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(ViewType.viewHolderLayout(viewType), parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item, position, onClick)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.type?.let {
            return it.ordinal
        }
        return 0
    }

    companion object {
        val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<ViewType>() {
            override fun areItemsTheSame(
                oldConcert: ViewType,
                newConcert: ViewType
            ) = oldConcert.type == newConcert.type

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldConcert: ViewType,
                newConcert: ViewType
            ) = oldConcert == newConcert
        }
    }
}