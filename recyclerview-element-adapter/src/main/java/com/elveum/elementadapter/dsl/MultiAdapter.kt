package com.elveum.elementadapter.dsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingHolder(
    val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root)

internal class MultiAdapter<T : Any>(
    private val concreteItemTypeScopesImpl: List<ConcreteItemTypeScopeImpl<T, ViewBinding>>,
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BindingHolder>(itemCallback) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val index = concreteItemTypeScopesImpl.indexOfFirst { it.predicate(item) }
        if (index == -1) {
            throw IllegalStateException("Have you covered all subtypes in the adapter { ... } " +
                    "by using addBinding<${item::class.java.canonicalName}, YOUR_BINDING> { ... } section?")
        }
        return index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val concreteTypeScope = concreteItemTypeScopesImpl[viewType]
        val viewBinding = concreteTypeScope.bindingCreator(inflater, parent)
        concreteTypeScope.uponCreating = true
        concreteTypeScope.listenersBlock?.invoke(viewBinding)
        concreteTypeScope.uponCreating = false
        return BindingHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val item = getItem(position)
        holder.binding.root.tag = item
        val concreteTypeScope = concreteItemTypeScopesImpl.first { it.predicate(item) }
        concreteTypeScope.viewsWithListeners.forEach {
            holder.binding.root.findViewById<View>(it)?.tag = item
        }
        concreteTypeScope.bindBlock?.invoke(holder.binding, item)
    }


}

