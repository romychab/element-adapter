package com.elveum.elementadapter.dsl

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.delegate.AdapterDelegate

class BindingHolder(
    val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root)

internal class MultiAdapter<T : Any>(
    private val adapterDelegate: AdapterDelegate<T>
) : ListAdapter<T, BindingHolder>(adapterDelegate.itemCallback()) {

    override fun getItemViewType(position: Int): Int {
        return adapterDelegate.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        return adapterDelegate.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(
        holder: BindingHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        adapterDelegate.onBindViewHolder(holder, getItem(position), payloads)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
    }

    override fun getItemId(position: Int): Long {
        val id = adapterDelegate.getItemId(getItem(position))
        if (hasStableIds() && id == RecyclerView.NO_ID) {
            throw IllegalStateException("stableId { ... } block is not implemented in addBinding<${getItem(position)::class.java.canonicalName}, YOUR_BINDING> { ... } section")
        }
        return id
    }
}

