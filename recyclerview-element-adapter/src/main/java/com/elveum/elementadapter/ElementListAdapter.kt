package com.elveum.elementadapter

import androidx.recyclerview.widget.*
import com.elveum.elementadapter.dsl.BindingHolder
import com.elveum.elementadapter.dsl.ElementWithIndex

abstract class ElementListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<ElementWithIndex<T>>,
) : RecyclerView.Adapter<BindingHolder>() {

    private val differ: AsyncListDiffer<ElementWithIndex<T>> = AsyncListDiffer(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    private val listener =
        AsyncListDiffer.ListListener<T> { previousList, currentList ->
            this@ElementListAdapter.onCurrentListChanged(
                previousList,
                currentList
            )
        }

    val currentList: List<T> get() = differ.currentList.map { it.element }

    fun submitList(list: List<T>?) {
        differ.submitList(list?.mapIndexed { index, t -> ElementWithIndex(index, t) })
    }

    fun submitList(list: List<T>?, commitCallback: Runnable?) {
        differ.submitList(list?.mapIndexed { index, t -> ElementWithIndex(index, t) }, commitCallback)
    }

    protected fun getItem(position: Int): T {
        return differ.currentList[position].element
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    open fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {}

}