package com.elveum.elementadapter.dsl

import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

typealias CompareItemCallback<T> = (oldItem: T, newItem: T) -> Boolean

internal class ItemCallbackDelegate<T : Any>(
    private val concreteItemTypeScopes: List<ConcreteItemTypeScopeImpl<T, ViewBinding>>
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        val oldScope = findScope(oldItem)
        val newScope = findScope(newItem)
        if (oldScope !== newScope) return false
        return newScope.areItemsSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        val oldScope = findScope(oldItem)
        val newScope = findScope(newItem)
        if (oldScope !== newScope) return false
        return newScope.areContentsSame(oldItem, newItem)
    }

    private fun findScope(item: T): ConcreteItemTypeScopeImpl<T, ViewBinding> {
        return concreteItemTypeScopes.first {
            it.predicate(item)
        }
    }

}
