package com.elveum.elementadapter.dsl

import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

typealias CompareItemCallback<T> = IndexScope<T>.(oldItem: T, newItem: T) -> Boolean

typealias ChangePayloadCallback<T> = IndexScope<T>.(oldItem: T, newItem: T) -> Any?

internal class ItemCallbackDelegate<T : Any>(
    private val adapterScope: AdapterScope<T>,
    private val concreteItemTypeScopes: List<ConcreteItemTypeScopeImpl<T, ViewBinding>>
) : DiffUtil.ItemCallback<ElementWithIndex<T>>() {

    override fun areItemsTheSame(oldItem: ElementWithIndex<T>, newItem: ElementWithIndex<T>): Boolean {
        val oldScope = findScope(oldItem.element)
        val newScope = findScope(newItem.element)
        val indexScope = IndexScopeImpl(oldItem, newItem)
        if (oldScope !== newScope) {
            return adapterScope.defaultAreItemsSame.invoke(indexScope, oldItem.element, newItem.element)
        }
        return newScope.areItemsSame(indexScope, oldItem.element, newItem.element)
    }

    override fun areContentsTheSame(oldItem: ElementWithIndex<T>, newItem: ElementWithIndex<T>): Boolean {
        val oldScope = findScope(oldItem.element)
        val newScope = findScope(newItem.element)
        val indexScope = IndexScopeImpl(oldItem, newItem)
        if (oldScope !== newScope) {
            return adapterScope.defaultAreContentsSame.invoke(indexScope, oldItem.element, newItem.element)
        }
        return newScope.areContentsSame(indexScope, oldItem.element, newItem.element)
    }

    override fun getChangePayload(oldItem: ElementWithIndex<T>, newItem: ElementWithIndex<T>): Any? {
        val oldScope = findScope(oldItem.element)
        val newScope = findScope(newItem.element)
        val indexScope = IndexScopeImpl(oldItem, newItem)
        if (oldScope !== newScope) {
            return adapterScope.defaultChangePayload.invoke(indexScope, oldItem.element, newItem.element)
        }
        return newScope.changePayload(indexScope, oldItem.element, newItem.element)
    }

    private fun findScope(item: T): ConcreteItemTypeScopeImpl<T, ViewBinding> {
        return concreteItemTypeScopes.first {
            it.predicate(item)
        }
    }

}
