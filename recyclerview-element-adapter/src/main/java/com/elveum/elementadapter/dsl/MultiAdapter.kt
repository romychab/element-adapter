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

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        adapterDelegate.onBindViewHolder(holder, getItem(position))
    }

}

