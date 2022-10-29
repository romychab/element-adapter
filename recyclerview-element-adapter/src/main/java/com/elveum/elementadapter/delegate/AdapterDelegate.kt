package com.elveum.elementadapter.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.dsl.BindingHolder
import com.elveum.elementadapter.dsl.ConcreteItemTypeScopeImpl

interface AdapterDelegate<T : Any> {

    /**
     * Use the [DiffUtil.ItemCallback] returned by this method in your own
     * adapter.
     */
    fun itemCallback(): DiffUtil.ItemCallback<T>

    /**
     * Call this method from [RecyclerView.Adapter.getItemViewType]
     */
    fun getItemViewType(item: T): Int

    /**
     * Call this method from [RecyclerView.Adapter.onCreateViewHolder]
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder

    /**
     * Call this method from [RecyclerView.Adapter.onBindViewHolder]
     */
    fun onBindViewHolder(holder: BindingHolder, item: T, payloads: List<Any> = emptyList())

}

internal class AdapterDelegateImpl<T : Any>(
    private val concreteItemTypeScopesImpl: List<ConcreteItemTypeScopeImpl<T, ViewBinding>>,
    private val itemCallback: DiffUtil.ItemCallback<T>
) : AdapterDelegate<T> {

    override fun itemCallback(): DiffUtil.ItemCallback<T> {
        return itemCallback
    }

    override fun getItemViewType(item: T): Int {
        val index = concreteItemTypeScopesImpl.indexOfFirst { it.predicate(item) }
        if (index == -1) {
            throw IllegalStateException("Have you covered all subtypes in the adapter { ... } " +
                    "by using addBinding<${item::class.java.canonicalName}, YOUR_BINDING> { ... } section?")
        }
        return index
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val concreteTypeScope = concreteItemTypeScopesImpl[viewType]
        val viewBinding = concreteTypeScope.bindingCreator(inflater, parent)
        concreteTypeScope.uponCreating = true
        concreteTypeScope.listenersBlock?.invoke(viewBinding)
        concreteTypeScope.uponCreating = false
        return BindingHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: BindingHolder, item: T, payloads: List<Any>) {
        holder.binding.root.tag = item
        val concreteTypeScope = concreteItemTypeScopesImpl.first { it.predicate(item) }
        concreteTypeScope.viewsWithListeners.forEach {
            holder.binding.root.findViewById<View>(it)?.tag = item
        }
        concreteTypeScope.bindBlock?.invoke(holder.binding, item, payloads)
    }

}