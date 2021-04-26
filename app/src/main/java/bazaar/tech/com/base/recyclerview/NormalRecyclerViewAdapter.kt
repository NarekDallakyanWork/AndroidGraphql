package bazaar.tech.com.base.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NormalRecyclerViewAdapter : RecyclerView.Adapter<MainViewHolder>() {

    private var viewTypeList = ArrayList<ViewType?>()
    private var onClick: ((ViewType) -> Unit?)? = null

    fun addListener(onClick: (data: ViewType) -> Unit?) {
        this.onClick = onClick
    }

    fun submitList(viewTypeList: ArrayList<ViewType?>) {
        this.viewTypeList = viewTypeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(ViewType.viewHolderLayout(viewType), parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewTypeList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = viewTypeList[position]
        holder.onBind(item, position, onClick)
    }

    override fun getItemViewType(position: Int): Int {
        viewTypeList[position]?.type?.let {
            return it.ordinal
        }
        return 0
    }
}