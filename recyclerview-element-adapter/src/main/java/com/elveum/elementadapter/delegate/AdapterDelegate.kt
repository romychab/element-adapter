package com.elveum.elementadapter.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.R
import com.elveum.elementadapter.dsl.BindingHolder
import com.elveum.elementadapter.dsl.ConcreteItemTypeScopeImpl
import com.elveum.elementadapter.dsl.ElementWithIndex

interface AdapterDelegate<T : Any> {

    /**
     * Use the [DiffUtil.ItemCallback] returned by this method in your own
     * adapter.
     */
    fun itemCallback(): DiffUtil.ItemCallback<ElementWithIndex<T>>

    /**
     * Use this item callback instead of [itemCallback] if you don't want to
     * support index() method.
     */
    fun noIndexItemCallback(): DiffUtil.ItemCallback<T>

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
    fun onBindViewHolder(holder: BindingHolder, position: Int, item: T, payloads: List<Any> = emptyList())

}

internal class AdapterDelegateImpl<T : Any>(
    private val concreteItemTypeScopesImpl: List<ConcreteItemTypeScopeImpl<T, ViewBinding>>,
    private val itemCallback: DiffUtil.ItemCallback<ElementWithIndex<T>>
) : AdapterDelegate<T> {

    override fun itemCallback(): DiffUtil.ItemCallback<ElementWithIndex<T>> {
        return itemCallback
    }

    override fun noIndexItemCallback(): DiffUtil.ItemCallback<T> {
        return itemWithoutIndexCallback
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

    override fun onBindViewHolder(holder: BindingHolder, position: Int, item: T, payloads: List<Any>) {
        holder.binding.root.setTag(R.id.element_entity_tag, item)
        holder.binding.root.setTag(R.id.element_index_tag, position)
        val concreteTypeScope = concreteItemTypeScopesImpl.first { it.predicate(item) }
        concreteTypeScope.viewsWithListeners.forEach {
            holder.binding.root.findViewById<View>(it)?.setTag(R.id.element_entity_tag, item)
            holder.binding.root.findViewById<View>(it)?.setTag(R.id.element_index_tag, position)
        }
        concreteTypeScope.currentIndex = position
        concreteTypeScope.bindBlock?.invoke(holder.binding, item, payloads)
        concreteTypeScope.currentIndex = null
    }

    private val itemWithoutIndexCallback = object : ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            val oldIndexItem = ElementWithIndex(-1, oldItem)
            val newIndexItem = ElementWithIndex(-1, newItem)
            return itemCallback.areItemsTheSame(oldIndexItem, newIndexItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            val oldIndexItem = ElementWithIndex(-1, oldItem)
            val newIndexItem = ElementWithIndex(-1, newItem)
            return itemCallback.areContentsTheSame(oldIndexItem, newIndexItem)
        }
    }

}